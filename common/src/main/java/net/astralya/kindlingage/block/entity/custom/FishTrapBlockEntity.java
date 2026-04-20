package net.astralya.kindlingage.block.entity.custom;

import java.util.Locale;
import java.util.Optional;
import net.astralya.kindlingage.block.custom.FishTrapBlock;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class FishTrapBlockEntity extends BlockEntity {
  public static final String CONFIG_CATEGORY = "blocks";
  public static final String CONFIG_CATCH_INTERVAL_KEY = "fishTrapCatchInterval";
  public static final int DEFAULT_CATCH_INTERVAL = 1200;
  public static final int MIN_CATCH_INTERVAL = 100;
  public static final int MAX_CATCH_INTERVAL = 72000;
  public static final int MAX_BAIT = 8;
  public static final int MAX_CATCH = 16;
  public static final double BASE_MIN = 0.125D;
  public static final double BASE_MAX = 0.875D;
  public static final double BASE_SPLIT_X = 0.5D;
  public static final double SEED_RENDER_X = (BASE_SPLIT_X + BASE_MAX) * 0.5D;
  public static final double FISH_RENDER_X = (BASE_MIN + BASE_SPLIT_X) * 0.5D;
  public static final double PILE_RENDER_Z = (BASE_MIN + BASE_MAX) * 0.5D;
  public static final double PILE_RENDER_Y = 0.203125D;
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

  public enum InteractionZone {
    NONE,
    SEED,
    FISH
  }

  public int getBaitCount() {
    return baitCount;
  }

  public ItemStack getCatchStack() {
    return catchStack;
  }

  public int getCatchCount() {
    return catchCount;
  }

  public boolean insertSeed(Player player, ItemStack stack) {
    if (baitCount >= MAX_BAIT) {
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
    giveOrDrop(player, catchStack.copyWithCount(1));
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

  public InteractionZone getInteractionZone(BlockHitResult hit, BlockState state) {
    Vec3 rotated = getRotatedHitLocation(hit, state);

    if (rotated.x < BASE_MIN
        || rotated.x > BASE_MAX
        || rotated.z < BASE_MIN
        || rotated.z > BASE_MAX) {
      return InteractionZone.NONE;
    }

    if (rotated.x >= BASE_SPLIT_X) {
      return InteractionZone.SEED;
    }

    return InteractionZone.FISH;
  }

  public Component getDebugMessage(BlockHitResult hit, BlockState state) {
    Vec3 local = getLocalHitLocation(hit);
    Vec3 rotated = getRotatedHitLocation(hit, state);
    InteractionZone zone = getInteractionZone(hit, state);
    return Component.literal(
        String.format(
            Locale.ROOT,
            "FishTrap hit local=(%.4f, %.4f, %.4f) northBasis=(%.4f, %.4f, %.4f) zone=%s seedRender=(%.4f, %.4f, %.4f) fishRender=(%.4f, %.4f, %.4f)",
            local.x,
            local.y,
            local.z,
            rotated.x,
            rotated.y,
            rotated.z,
            zone.name(),
            SEED_RENDER_X,
            PILE_RENDER_Y,
            PILE_RENDER_Z,
            FISH_RENDER_X,
            PILE_RENDER_Y,
            PILE_RENDER_Z));
  }

  public static void serverTick(
      Level level, BlockPos pos, BlockState state, FishTrapBlockEntity blockEntity) {
    if (!state.getValue(FishTrapBlock.WATERLOGGED)) {
      if (blockEntity.progress != 0) {
        blockEntity.progress = 0;
        blockEntity.setChanged();
      }
      return;
    }

    if (blockEntity.baitCount <= 0 || blockEntity.catchCount >= MAX_CATCH) {
      if (blockEntity.progress != 0) {
        blockEntity.progress = 0;
        blockEntity.setChanged();
      }
      return;
    }

    Optional<ItemStack> generatedCatch = blockEntity.getGeneratedCatch(level, pos);
    if (generatedCatch.isEmpty() || !blockEntity.canStoreCatch(generatedCatch.get())) {
      if (blockEntity.progress != 0) {
        blockEntity.progress = 0;
        blockEntity.setChanged();
      }
      return;
    }

    blockEntity.progress++;
    if (blockEntity.progress < catchInterval) {
      blockEntity.setChanged();
      return;
    }

    blockEntity.progress = 0;
    blockEntity.baitCount--;
    blockEntity.addCatch(generatedCatch.get());
    blockEntity.markUpdated();
  }

  public static void clientTick(
      Level level, BlockPos pos, BlockState state, FishTrapBlockEntity blockEntity) {}

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putInt("BaitCount", baitCount);
    tag.putInt("CatchCount", catchCount);
    tag.putInt("Progress", progress);
    if (!catchStack.isEmpty()) {
      tag.put("CatchStack", catchStack.save(registries));
    }
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    baitCount = Mth.clamp(tag.getInt("BaitCount"), 0, MAX_BAIT);
    catchCount = Mth.clamp(tag.getInt("CatchCount"), 0, MAX_CATCH);
    progress = Math.max(tag.getInt("Progress"), 0);
    catchStack =
        tag.contains("CatchStack")
            ? ItemStack.parse(registries, tag.getCompound("CatchStack")).orElse(ItemStack.EMPTY)
            : ItemStack.EMPTY;
    if (catchCount == 0) {
      catchStack = ItemStack.EMPTY;
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

  private void addCatch(ItemStack generated) {
    if (catchStack.isEmpty()) {
      catchStack = generated;
      catchCount = 1;
      return;
    }

    if (ItemStack.isSameItemSameComponents(catchStack, generated)) {
      catchCount = Math.min(MAX_CATCH, catchCount + 1);
    }
  }

  private Optional<ItemStack> getGeneratedCatch(Level level, BlockPos pos) {
    net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> biome = level.getBiome(pos);
    if (biome.is(BiomeTags.IS_RIVER)) {
      return Optional.of(new ItemStack(Items.SALMON));
    }

    if (biome.is(BiomeTags.IS_OCEAN)) {
      return Optional.of(new ItemStack(Items.COD));
    }

    return Optional.empty();
  }

  private boolean canStoreCatch(ItemStack generated) {
    return catchStack.isEmpty() || ItemStack.isSameItemSameComponents(catchStack, generated);
  }

  private Vec3 getLocalHitLocation(BlockHitResult hit) {
    return hit.getLocation().subtract(Vec3.atLowerCornerOf(worldPosition));
  }

  private Vec3 getRotatedHitLocation(BlockHitResult hit, BlockState state) {
    return rotateToNorth(getLocalHitLocation(hit), state.getValue(FishTrapBlock.FACING));
  }

  private static Vec3 rotateToNorth(Vec3 local, Direction facing) {
    double x = local.x;
    double z = local.z;
    return switch (facing) {
      case NORTH -> new Vec3(x, local.y, z);
      case SOUTH -> new Vec3(1.0D - x, local.y, 1.0D - z);
      case EAST -> new Vec3(z, local.y, 1.0D - x);
      case WEST -> new Vec3(1.0D - z, local.y, x);
      default -> local;
    };
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
