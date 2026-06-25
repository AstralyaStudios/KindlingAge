package net.astralya.kindlingage.block.entity.custom;

import net.astralya.kindlingage.block.custom.FishTrapBlock;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class FishTrapBlockEntity extends BlockEntity {
  public static final String CONFIG_CATEGORY = "fish_trap";
  public static final String CONFIG_CATCH_INTERVAL_KEY = "catchInterval";
  public static final int DEFAULT_CATCH_INTERVAL = 1200;
  public static final int MIN_CATCH_INTERVAL = 100;
  public static final int MAX_CATCH_INTERVAL = 72000;
  public static final int MAX_BAIT = 8;
  public static final int MAX_CATCH = 16;

  private static int catchInterval = DEFAULT_CATCH_INTERVAL;

  private int baitCount;
  private int catchCount;
  private int progress;
  private ItemStack catchStack = ItemStack.EMPTY;

  public FishTrapBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.FISH_TRAP.get(), pos, state);
  }

  public static void applyConfig(int catchIntervalTicks) {
    catchInterval = Mth.clamp(catchIntervalTicks, MIN_CATCH_INTERVAL, MAX_CATCH_INTERVAL);
  }

  public static int getCatchInterval() {
    return catchInterval;
  }

  public int getBaitCount() {
    return baitCount;
  }

  public int getCatchCount() {
    return catchCount;
  }

  public ItemStack getCatchStack() {
    return catchStack;
  }

  public boolean insertSeed(Player player, ItemStack stack) {
    if (baitCount >= MAX_BAIT || !stack.is(ModItems.WILD_SEEDS.get())) {
      return false;
    }

    baitCount++;
    if (!player.getAbilities().instabuild) {
      stack.shrink(1);
    }
    markUpdated();
    return true;
  }

  public boolean extractSeed(Player player) {
    if (baitCount <= 0) {
      return false;
    }

    baitCount--;
    if (baitCount == 0) {
      progress = 0;
    }
    giveOrDrop(player, new ItemStack(ModItems.WILD_SEEDS.get()));
    markUpdated();
    return true;
  }

  public boolean extractFish(Player player) {
    if (catchCount <= 0 || catchStack.isEmpty()) {
      return false;
    }

    catchCount--;
    giveOrDrop(player, catchStack.copy());
    if (catchCount == 0) {
      catchStack = ItemStack.EMPTY;
    }
    markUpdated();
    return true;
  }

  public void dropContents(Level level, BlockPos pos) {
    if (baitCount > 0) {
      Containers.dropItemStack(
          level,
          pos.getX() + 0.5D,
          pos.getY() + 0.25D,
          pos.getZ() + 0.5D,
          new ItemStack(ModItems.WILD_SEEDS.get(), baitCount));
    }

    if (catchCount > 0 && !catchStack.isEmpty()) {
      Containers.dropItemStack(
          level,
          pos.getX() + 0.5D,
          pos.getY() + 0.25D,
          pos.getZ() + 0.5D,
          catchStack.copyWithCount(catchCount));
    }
  }

  public static void serverTick(
      Level level, BlockPos pos, BlockState state, FishTrapBlockEntity blockEntity) {
    if (!state.getValue(FishTrapBlock.WATERLOGGED)) {
      blockEntity.resetProgressIfNeeded();
      return;
    }

    if (blockEntity.baitCount <= 0 || blockEntity.catchCount >= MAX_CATCH) {
      blockEntity.resetProgressIfNeeded();
      return;
    }

    ItemStack generatedCatch = blockEntity.getGeneratedCatch(level, pos);
    if (generatedCatch.isEmpty() || !blockEntity.canStoreCatch(generatedCatch)) {
      blockEntity.resetProgressIfNeeded();
      return;
    }

    blockEntity.progress++;
    if (blockEntity.progress < catchInterval) {
      blockEntity.setChanged();
      return;
    }

    blockEntity.progress = 0;
    blockEntity.baitCount--;
    blockEntity.addCatch(generatedCatch);
    blockEntity.markUpdated();
  }

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    tag.putInt("BaitCount", baitCount);
    tag.putInt("CatchCount", catchCount);
    tag.putInt("Progress", progress);
    if (!catchStack.isEmpty()) {
      tag.put("CatchStack", catchStack.save(new CompoundTag()));
    }
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    baitCount = Mth.clamp(tag.getInt("BaitCount"), 0, MAX_BAIT);
    catchCount = Mth.clamp(tag.getInt("CatchCount"), 0, MAX_CATCH);
    progress = Math.max(tag.getInt("Progress"), 0);
    catchStack =
        tag.contains("CatchStack") ? ItemStack.of(tag.getCompound("CatchStack")) : ItemStack.EMPTY;
    if (catchCount == 0) {
      catchStack = ItemStack.EMPTY;
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

  private void resetProgressIfNeeded() {
    if (progress != 0) {
      progress = 0;
      setChanged();
    }
  }

  private void addCatch(ItemStack generated) {
    if (catchStack.isEmpty()) {
      catchStack = generated;
      catchCount = 1;
      return;
    }

    if (ItemStack.isSameItemSameTags(catchStack, generated)) {
      catchCount = Math.min(MAX_CATCH, catchCount + 1);
    }
  }

  private ItemStack getGeneratedCatch(Level level, BlockPos pos) {
    var biome = level.getBiome(pos);
    if (biome.is(BiomeTags.IS_RIVER)) {
      return new ItemStack(Items.SALMON);
    }

    if (biome.is(BiomeTags.IS_OCEAN)) {
      return new ItemStack(Items.COD);
    }

    return new ItemStack(Items.COD);
  }

  private boolean canStoreCatch(ItemStack generated) {
    return catchStack.isEmpty() || ItemStack.isSameItemSameTags(catchStack, generated);
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
