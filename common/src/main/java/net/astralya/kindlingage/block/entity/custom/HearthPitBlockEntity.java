package net.astralya.kindlingage.block.entity.custom;

import java.util.Optional;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public final class HearthPitBlockEntity extends BlockEntity implements Clearable {
  public static final int SLOT_COUNT = 2;
  public static final int FUEL_PER_KINDLING = 6;
  private static final float COOK_TIME_MULTIPLIER = 0.5F;

  private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
  private final int[] cookingProgress = new int[SLOT_COUNT];
  private final int[] cookingTime = new int[SLOT_COUNT];
  private final boolean[] fueledSlots = new boolean[SLOT_COUNT];
  private final RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> quickCheck =
      RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

  private int remainingFuel;

  public HearthPitBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.HEARTH_PIT.get(), pos, state);
  }

  public static int adjustedCookTime(int originalCookTime) {
    return Math.max(1, Math.round(originalCookTime * COOK_TIME_MULTIPLIER));
  }

  public NonNullList<ItemStack> getItems() {
    return items;
  }

  public Optional<RecipeHolder<CampfireCookingRecipe>> getCookableRecipe(ItemStack stack) {
    boolean hasEmptySlot = items.stream().anyMatch(ItemStack::isEmpty);
    if (!hasEmptySlot || level == null) {
      return Optional.empty();
    }

    return quickCheck.getRecipeFor(new SingleRecipeInput(stack), level);
  }

  public boolean addKindling(net.minecraft.world.entity.player.Player user, ItemStack stack) {
    remainingFuel += FUEL_PER_KINDLING;
    if (!user.level().isClientSide && !user.getAbilities().instabuild) {
      stack.shrink(1);
    }
    updateLitState();
    markUpdated();
    return true;
  }

  public boolean placeFood(LivingEntity user, ItemStack stack, int totalCookTime) {
    for (int slot = 0; slot < items.size(); slot++) {
      if (!items.get(slot).isEmpty()) {
        continue;
      }

      cookingTime[slot] = totalCookTime;
      cookingProgress[slot] = 0;
      fueledSlots[slot] = false;
      items.set(slot, stack.consumeAndReturn(1, user));

      if (level != null) {
        level.gameEvent(
            GameEvent.BLOCK_CHANGE, worldPosition, GameEvent.Context.of(user, getBlockState()));
      }
      markUpdated();
      return true;
    }

    return false;
  }

  public void extinguish() {
    remainingFuel = 0;
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      fueledSlots[slot] = false;
    }
    updateLitState();
    markUpdated();
  }

  public static void serverTick(
      Level level, BlockPos pos, BlockState state, HearthPitBlockEntity blockEntity) {
    if (!state.getValue(CampfireBlock.LIT)) {
      return;
    }

    boolean changed = false;
    boolean visualChanged = false;

    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      ItemStack stack = blockEntity.items.get(slot);
      if (stack.isEmpty()) {
        if (blockEntity.cookingProgress[slot] != 0 || blockEntity.fueledSlots[slot]) {
          blockEntity.cookingProgress[slot] = 0;
          blockEntity.cookingTime[slot] = 0;
          blockEntity.fueledSlots[slot] = false;
          changed = true;
        }
        continue;
      }

      if (!blockEntity.fueledSlots[slot]) {
        if (blockEntity.remainingFuel <= 0) {
          continue;
        }
        blockEntity.remainingFuel--;
        blockEntity.fueledSlots[slot] = true;
        changed = true;
      }

      changed = true;
      blockEntity.cookingProgress[slot]++;

      if (blockEntity.cookingProgress[slot] < blockEntity.cookingTime[slot]) {
        continue;
      }

      SingleRecipeInput input = new SingleRecipeInput(stack);
      ItemStack result =
          blockEntity
              .quickCheck
              .getRecipeFor(input, level)
              .map(recipe -> recipe.value().assemble(input, level.registryAccess()))
              .orElse(stack.copy());

      if (result.isItemEnabled(level.enabledFeatures())) {
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), result);
      }

      blockEntity.items.set(slot, ItemStack.EMPTY);
      blockEntity.cookingProgress[slot] = 0;
      blockEntity.cookingTime[slot] = 0;
      blockEntity.fueledSlots[slot] = false;
      level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
      visualChanged = true;
    }

    blockEntity.updateLitState();
    if (changed) {
      blockEntity.setChanged();
    }
    if (visualChanged) {
      blockEntity.markUpdated();
    }
  }

  public static void clientTick(
      Level level, BlockPos pos, BlockState state, HearthPitBlockEntity blockEntity) {
    if (!state.getValue(CampfireBlock.LIT)) {
      return;
    }

    RandomSource random = level.random;
    int facing = state.getValue(CampfireBlock.FACING).get2DDataValue();

    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      if (blockEntity.items.get(slot).isEmpty() || random.nextFloat() >= 0.2F) {
        continue;
      }

      net.minecraft.core.Direction direction =
          net.minecraft.core.Direction.from2DDataValue(Math.floorMod(facing + slot * 2, 4));
      double x =
          pos.getX()
              + 0.5D
              - direction.getStepX() * 0.3125D
              + direction.getClockWise().getStepX() * 0.3125D;
      double y = pos.getY() + 0.5D;
      double z =
          pos.getZ()
              + 0.5D
              - direction.getStepZ() * 0.3125D
              + direction.getClockWise().getStepZ() * 0.3125D;

      for (int i = 0; i < 4; i++) {
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 5.0E-4D, 0.0D);
      }
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    net.minecraft.world.ContainerHelper.saveAllItems(tag, items, true, registries);
    tag.putIntArray("CookingTimes", cookingProgress);
    tag.putIntArray("CookingTotalTimes", cookingTime);
    tag.putInt("RemainingFuel", remainingFuel);
    int[] fueled = new int[SLOT_COUNT];
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      fueled[slot] = fueledSlots[slot] ? 1 : 0;
    }
    tag.putIntArray("FueledSlots", fueled);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    items.clear();
    net.minecraft.world.ContainerHelper.loadAllItems(tag, items, registries);

    int[] progress = tag.getIntArray("CookingTimes");
    int[] totals = tag.getIntArray("CookingTotalTimes");
    int[] fueled = tag.getIntArray("FueledSlots");

    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      cookingProgress[slot] = slot < progress.length ? Math.max(progress[slot], 0) : 0;
      cookingTime[slot] = slot < totals.length ? Math.max(totals[slot], 0) : 0;
      fueledSlots[slot] = slot < fueled.length && fueled[slot] > 0;
    }

    remainingFuel = Math.max(tag.getInt("RemainingFuel"), 0);
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

  @Override
  public void clearContent() {
    items.clear();
    for (int slot = 0; slot < SLOT_COUNT; slot++) {
      cookingProgress[slot] = 0;
      cookingTime[slot] = 0;
      fueledSlots[slot] = false;
    }
    remainingFuel = 0;
    updateLitState();
    setChanged();
  }

  private boolean hasFueledItems() {
    for (boolean fueledSlot : fueledSlots) {
      if (fueledSlot) {
        return true;
      }
    }
    return false;
  }

  private void updateLitState() {
    if (level == null) {
      return;
    }

    BlockState state = getBlockState();
    boolean shouldBeLit =
        !state.getValue(CampfireBlock.WATERLOGGED) && (remainingFuel > 0 || hasFueledItems());
    if (state.getValue(CampfireBlock.LIT) == shouldBeLit) {
      return;
    }

    level.setBlock(worldPosition, state.setValue(CampfireBlock.LIT, shouldBeLit), 3);
  }

  private void markUpdated() {
    setChanged();

    if (level != null && !level.isClientSide) {
      BlockState state = getBlockState();
      level.sendBlockUpdated(worldPosition, state, state, 3);
    }
  }
}
