package net.astralya.kindlingage.block.entity.custom;

import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.recipe.ModRecipeTypes;
import net.astralya.kindlingage.recipe.custom.DryingRackRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class DryingRackBlockEntity extends BlockEntity {
  public static final int SLOT_COUNT = 6;

  private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
  private final NonNullList<ItemStack> results = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
  private final int[] progress = new int[SLOT_COUNT];
  private final int[] dryTimes = new int[SLOT_COUNT];
  private final RecipeManager.CachedCheck<Container, DryingRackRecipe> quickCheck =
      RecipeManager.createCheck(ModRecipeTypes.DRYING.get());

  public DryingRackBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.DRYING_RACK.get(), pos, state);
  }

  public boolean canExtractAny() {
    return findExtractSlot() >= 0;
  }

  public ItemStack getRenderItem(int slot) {
    if (slot < 0 || slot >= SLOT_COUNT) {
      return ItemStack.EMPTY;
    }

    return items.get(slot);
  }

  public boolean canAcceptItem(ItemStack stack) {
    return !stack.isEmpty() && findEmptySlot() >= 0 && getRecipe(stack) != null;
  }

  public boolean insertItem(Player player, ItemStack stack) {
    if (!canAcceptItem(stack)) {
      return false;
    }

    int slot = findEmptySlot();
    DryingRackRecipe recipe = getRecipe(stack);
    if (slot < 0 || recipe == null) {
      return false;
    }

    items.set(slot, stack.split(1));
    results.set(slot, recipe.result().copy());
    dryTimes[slot] = recipe.duration();
    progress[slot] = 0;
    markUpdated();
    return true;
  }

  public boolean extractNextItem(Player player) {
    int slot = findExtractSlot();
    if (slot < 0) {
      return false;
    }

    ItemStack stack = items.get(slot);
    if (stack.isEmpty()) {
      return false;
    }

    items.set(slot, ItemStack.EMPTY);
    results.set(slot, ItemStack.EMPTY);
    progress[slot] = 0;
    dryTimes[slot] = 0;
    giveOrDrop(player, stack);
    markUpdated();
    return true;
  }

  public void dropContents(Level level, BlockPos pos) {
    Containers.dropContents(level, pos, items);
  }

  public static void serverTick(
      Level level, BlockPos pos, BlockState state, DryingRackBlockEntity blockEntity) {
    boolean dirty = false;
    boolean visualChanged = false;

    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      ItemStack stack = blockEntity.items.get(slot);
      if (stack.isEmpty()) {
        if (blockEntity.progress[slot] != 0
            || blockEntity.dryTimes[slot] != 0
            || !blockEntity.results.get(slot).isEmpty()) {
          blockEntity.progress[slot] = 0;
          blockEntity.dryTimes[slot] = 0;
          blockEntity.results.set(slot, ItemStack.EMPTY);
          dirty = true;
        }
        continue;
      }

      if (blockEntity.results.get(slot).isEmpty() || blockEntity.dryTimes[slot] <= 0) {
        continue;
      }

      blockEntity.progress[slot]++;
      dirty = true;
      if (blockEntity.progress[slot] < blockEntity.dryTimes[slot]) {
        continue;
      }

      blockEntity.items.set(slot, blockEntity.results.get(slot).copy());
      blockEntity.results.set(slot, ItemStack.EMPTY);
      blockEntity.progress[slot] = 0;
      blockEntity.dryTimes[slot] = 0;
      visualChanged = true;
    }

    if (visualChanged) {
      blockEntity.markUpdated();
    } else if (dirty) {
      blockEntity.setChanged();
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    ContainerHelper.saveAllItems(tag, items, true);

    CompoundTag resultsTag = new CompoundTag();
    ContainerHelper.saveAllItems(resultsTag, results, true);
    tag.put("Results", resultsTag);
    tag.putIntArray("Progress", progress);
    tag.putIntArray("DryTimes", dryTimes);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    items.clear();
    ContainerHelper.loadAllItems(tag, items);

    results.clear();
    if (tag.contains("Results")) {
      ContainerHelper.loadAllItems(tag.getCompound("Results"), results);
    }

    int[] savedProgress = tag.getIntArray("Progress");
    int[] savedDryTimes = tag.getIntArray("DryTimes");
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      progress[slot] = slot < savedProgress.length ? Math.max(savedProgress[slot], 0) : 0;
      dryTimes[slot] = slot < savedDryTimes.length ? Math.max(savedDryTimes[slot], 0) : 0;
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

  private DryingRackRecipe getRecipe(ItemStack stack) {
    if (level == null) {
      return null;
    }

    return quickCheck.getRecipeFor(new SimpleContainer(stack), level).orElse(null);
  }

  private int findEmptySlot() {
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      if (items.get(slot).isEmpty()) {
        return slot;
      }
    }

    return -1;
  }

  private int findExtractSlot() {
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      if (!items.get(slot).isEmpty() && results.get(slot).isEmpty()) {
        return slot;
      }
    }

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
