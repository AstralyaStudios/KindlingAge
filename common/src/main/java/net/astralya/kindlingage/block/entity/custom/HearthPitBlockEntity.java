package net.astralya.kindlingage.block.entity.custom;

import net.astralya.kindlingage.block.custom.HearthPitBlock;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class HearthPitBlockEntity extends BlockEntity {
  public static final int SLOT_COUNT = 2;
  private static final float COOK_TIME_MULTIPLIER = 0.5F;

  private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
  private final int[] cookingProgress = new int[SLOT_COUNT];
  private final int[] cookingTime = new int[SLOT_COUNT];
  private final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> quickCheck =
      RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

  public HearthPitBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.HEARTH_PIT.get(), pos, state);
  }

  public static int adjustedCookTime(int originalCookTime) {
    return Math.max(1, Math.round(originalCookTime * COOK_TIME_MULTIPLIER));
  }

  public boolean canAccept(ItemStack stack) {
    return findEmptySlot() >= 0 && getCookableRecipe(stack) != null;
  }

  public NonNullList<ItemStack> getItems() {
    return items;
  }

  public ItemStack getRenderItem(int slot) {
    if (slot < 0 || slot >= SLOT_COUNT) {
      return ItemStack.EMPTY;
    }

    return items.get(slot);
  }

  public CampfireCookingRecipe getCookableRecipe(ItemStack stack) {
    if (findEmptySlot() < 0 || level == null) {
      return null;
    }

    return quickCheck.getRecipeFor(new SimpleContainer(stack), level).orElse(null);
  }

  public boolean insertItem(Player player, ItemStack stack, int totalCookTime) {
    if (stack.isEmpty() || totalCookTime <= 0 || getCookableRecipe(stack) == null) {
      return false;
    }

    int slot = findEmptySlot();
    if (slot < 0) {
      return false;
    }

    items.set(slot, stack.split(1));
    cookingProgress[slot] = 0;
    cookingTime[slot] = totalCookTime;
    markUpdated();
    return true;
  }

  public boolean canExtract() {
    return findOccupiedSlot() >= 0;
  }

  public boolean extractItem(Player player) {
    int slot = findOccupiedSlot();
    if (slot < 0) {
      return false;
    }

    giveOrDrop(player, items.get(slot));
    items.set(slot, ItemStack.EMPTY);
    cookingProgress[slot] = 0;
    cookingTime[slot] = 0;
    markUpdated();
    return true;
  }

  public void dropContents(Level level, BlockPos pos) {
    Containers.dropContents(level, pos, items);
    items.clear();
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      cookingProgress[slot] = 0;
      cookingTime[slot] = 0;
    }
  }

  public static void serverTick(
      Level level, BlockPos pos, BlockState state, HearthPitBlockEntity blockEntity) {
    if (!state.getValue(HearthPitBlock.LIT)) {
      return;
    }

    boolean dirty = false;
    boolean visualChanged = false;
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      ItemStack stack = blockEntity.items.get(slot);
      if (stack.isEmpty()) {
        if (blockEntity.cookingProgress[slot] != 0 || blockEntity.cookingTime[slot] != 0) {
          blockEntity.cookingProgress[slot] = 0;
          blockEntity.cookingTime[slot] = 0;
          dirty = true;
        }
        continue;
      }

      CampfireCookingRecipe recipe =
          blockEntity.quickCheck.getRecipeFor(new SimpleContainer(stack), level).orElse(null);
      if (recipe == null) {
        continue;
      }

      blockEntity.cookingProgress[slot]++;
      dirty = true;
      if (blockEntity.cookingTime[slot] <= 0) {
        blockEntity.cookingTime[slot] = adjustedCookTime(recipe.getCookingTime());
      }

      if (blockEntity.cookingProgress[slot] >= blockEntity.cookingTime[slot]) {
        ItemStack result = assemble(recipe, stack, level.registryAccess());
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), result);
        blockEntity.items.set(slot, ItemStack.EMPTY);
        blockEntity.cookingProgress[slot] = 0;
        blockEntity.cookingTime[slot] = 0;
        visualChanged = true;
      }
    }

    if (visualChanged) {
      blockEntity.markUpdated();
    } else if (dirty) {
      blockEntity.setChanged();
    }
  }

  public static void clientTick(
      Level level, BlockPos pos, BlockState state, HearthPitBlockEntity blockEntity) {
    if (!state.getValue(HearthPitBlock.LIT)) {
      return;
    }

    RandomSource random = level.random;
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      if (blockEntity.items.get(slot).isEmpty() || random.nextFloat() >= 0.2F) {
        continue;
      }

      double x = pos.getX() + (slot == 0 ? 5.0D / 16.0D : 11.0D / 16.0D);
      double y = pos.getY() + 0.5D;
      double z = pos.getZ() + (slot == 0 ? 9.0D / 16.0D : 8.0D / 16.0D);

      for (int i = 0; i < 4; i++) {
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 5.0E-4D, 0.0D);
      }
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    ContainerHelper.saveAllItems(tag, items, true);
    tag.putIntArray("CookingProgress", cookingProgress);
    tag.putIntArray("CookingTime", cookingTime);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    items.clear();
    if (tag.contains("Item", 10)) {
      items.set(0, ItemStack.of(tag.getCompound("Item")));
    } else {
      ContainerHelper.loadAllItems(tag, items);
    }

    int[] savedProgress = tag.getIntArray("CookingProgress");
    int[] savedTimes = tag.getIntArray("CookingTime");
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      cookingProgress[slot] = slot < savedProgress.length ? Math.max(savedProgress[slot], 0) : 0;
      cookingTime[slot] = slot < savedTimes.length ? Math.max(savedTimes[slot], 0) : 0;
    }
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag tag = new CompoundTag();
    saveAdditional(tag);
    return tag;
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  private static ItemStack assemble(
      CampfireCookingRecipe recipe, ItemStack stack, RegistryAccess registryAccess) {
    return recipe.assemble(new SimpleContainer(stack), registryAccess);
  }

  private int findEmptySlot() {
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      if (items.get(slot).isEmpty()) {
        return slot;
      }
    }

    return -1;
  }

  private int findOccupiedSlot() {
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      if (!items.get(slot).isEmpty()) {
        return slot;
      }
    }

    return -1;
  }

  private void giveOrDrop(Player player, ItemStack stack) {
    if (!player.getInventory().add(stack)) {
      player.drop(stack, false);
    }
  }

  private void markUpdated() {
    setChanged();

    if (level != null && !level.isClientSide) {
      BlockState state = getBlockState();
      level.sendBlockUpdated(worldPosition, state, state, 3);
    }
  }
}
