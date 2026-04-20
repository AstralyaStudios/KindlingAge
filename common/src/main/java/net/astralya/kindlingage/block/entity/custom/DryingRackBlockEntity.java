package net.astralya.kindlingage.block.entity.custom;

import net.astralya.kindlingage.block.custom.DryingRackBlock;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.recipe.ModRecipeTypes;
import net.astralya.kindlingage.recipe.custom.DryingRackRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class DryingRackBlockEntity extends BlockEntity {
  public static final int SLOT_COUNT = 6;
  private static final double[] SLOT_X = {4.0D / 16.0D, 8.0D / 16.0D, 12.0D / 16.0D};
  private static final double[] SLOT_Y = {11.0D / 16.0D, 6.0D / 16.0D};
  private static final double SLOT_Z = 3.1D / 16.0D;

  private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
  private final NonNullList<ItemStack> results = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
  private final int[] progress = new int[SLOT_COUNT];
  private final int[] dryTimes = new int[SLOT_COUNT];
  private final RecipeManager.CachedCheck<SingleRecipeInput, DryingRackRecipe> quickCheck =
      RecipeManager.createCheck(ModRecipeTypes.DRYING.get());

  public DryingRackBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.DRYING_RACK.get(), pos, state);
  }

  public ItemStack getItem(int slot) {
    if (slot < 0 || slot >= SLOT_COUNT) {
      return ItemStack.EMPTY;
    }

    return items.get(slot);
  }

  public boolean hasItem(int slot) {
    return !getItem(slot).isEmpty();
  }

  public boolean canExtractAny() {
    return findExtractSlot() >= 0;
  }

  public boolean canAcceptItem(ItemStack stack) {
    if (stack.isEmpty() || level == null || findEmptySlot() < 0) {
      return false;
    }

    return getRecipe(stack).isPresent();
  }

  public boolean insertItem(Player player, ItemStack stack) {
    if (!canAcceptItem(stack)) {
      return false;
    }

    int slot = findEmptySlot();
    if (slot < 0) {
      return false;
    }

    RecipeHolder<DryingRackRecipe> recipe = getRecipe(stack).orElseThrow();
    items.set(slot, stack.consumeAndReturn(1, player));
    results.set(slot, recipe.value().result().copy());
    dryTimes[slot] = recipe.value().duration();
    progress[slot] = 0;
    markUpdated();
    return true;
  }

  public boolean extractNextItem(Player player) {
    int slot = findExtractSlot();
    if (slot < 0 || slot >= SLOT_COUNT) {
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

      ItemStack driedStack = blockEntity.results.get(slot).copy();
      blockEntity.items.set(slot, driedStack);
      blockEntity.results.set(slot, ItemStack.EMPTY);
      blockEntity.progress[slot] = 0;
      blockEntity.dryTimes[slot] = 0;
      if (level instanceof ServerLevel serverLevel) {
        emitDryingFinishedEffects(serverLevel, pos, state, slot, driedStack);
      }
      visualChanged = true;
    }

    if (visualChanged) {
      blockEntity.markUpdated();
    } else if (dirty) {
      blockEntity.setChanged();
    }
  }

  public static void clientTick(
      Level level, BlockPos pos, BlockState state, DryingRackBlockEntity blockEntity) {}

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    net.minecraft.world.ContainerHelper.saveAllItems(tag, items, true, registries);

    CompoundTag resultsTag = new CompoundTag();
    net.minecraft.world.ContainerHelper.saveAllItems(resultsTag, results, true, registries);
    tag.put("Results", resultsTag);
    tag.putIntArray("Progress", progress);
    tag.putIntArray("DryTimes", dryTimes);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    items.clear();
    net.minecraft.world.ContainerHelper.loadAllItems(tag, items, registries);

    results.clear();
    if (tag.contains("Results")) {
      net.minecraft.world.ContainerHelper.loadAllItems(
          tag.getCompound("Results"), results, registries);
    }

    int[] savedProgress = tag.getIntArray("Progress");
    int[] savedDryTimes = tag.getIntArray("DryTimes");
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      progress[slot] = slot < savedProgress.length ? Math.max(savedProgress[slot], 0) : 0;
      dryTimes[slot] = slot < savedDryTimes.length ? Math.max(savedDryTimes[slot], 0) : 0;
    }
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = new CompoundTag();
    saveAdditional(tag, registries);
    return tag;
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  private java.util.Optional<RecipeHolder<DryingRackRecipe>> getRecipe(ItemStack stack) {
    if (level == null) {
      return java.util.Optional.empty();
    }

    return quickCheck.getRecipeFor(new SingleRecipeInput(stack), level);
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

  private static void emitDryingFinishedEffects(
      ServerLevel level, BlockPos pos, BlockState state, int slot, ItemStack driedStack) {
    double[] slotPos = getSlotWorldPosition(state.getValue(DryingRackBlock.FACING), slot);
    double x = pos.getX() + slotPos[0];
    double y = pos.getY() + slotPos[1];
    double z = pos.getZ() + slotPos[2];

    level.sendParticles(ParticleTypes.CLOUD, x, y, z, 2, 0.03D, 0.03D, 0.03D, 0.005D);
    level.sendParticles(
        new ItemParticleOption(ParticleTypes.ITEM, driedStack),
        x,
        y,
        z,
        3,
        0.03D,
        0.03D,
        0.03D,
        0.01D);

    float pitch = 0.9F + level.getRandom().nextFloat() * 0.2F;
    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6F, pitch);
  }

  private static double[] getSlotWorldPosition(Direction facing, int slot) {
    int row = slot / 3;
    int column = slot % 3;
    double x = SLOT_X[column];
    double y = SLOT_Y[row];
    double z = SLOT_Z;

    return switch (facing) {
      case NORTH -> new double[] {x, y, z};
      case SOUTH -> new double[] {1.0D - x, y, 1.0D - z};
      case EAST -> new double[] {1.0D - z, y, x};
      case WEST -> new double[] {z, y, 1.0D - x};
      default -> new double[] {x, y, z};
    };
  }

  private void markUpdated() {
    setChanged();

    if (level != null && !level.isClientSide) {
      BlockState state = getBlockState();
      level.sendBlockUpdated(worldPosition, state, state, 3);
    }
  }
}
