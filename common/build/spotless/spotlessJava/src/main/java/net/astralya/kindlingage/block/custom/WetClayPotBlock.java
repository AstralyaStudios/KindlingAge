package net.astralya.kindlingage.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.block.entity.custom.ClayPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class WetClayPotBlock extends BaseEntityBlock {
  public static final MapCodec<WetClayPotBlock> CODEC = simpleCodec(WetClayPotBlock::new);

  private static final VoxelShape SHAPE =
      Shapes.or(
          Shapes.box(0.1875, 0, 0.1875, 0.8125, 0.0625, 0.8125),
          Shapes.box(0.75, 0.0625, 0.1875, 0.8125, 0.4375, 0.8125),
          Shapes.box(0.25, 0.0625, 0.1875, 0.75, 0.4375, 0.25),
          Shapes.box(0.25, 0.0625, 0.75, 0.75, 0.4375, 0.8125),
          Shapes.box(0.1875, 0.0625, 0.1875, 0.25, 0.4375, 0.8125),
          Shapes.box(0.25, 0.4375, 0.75, 0.75, 0.5625, 0.875),
          Shapes.box(0.25, 0.4375, 0.125, 0.75, 0.5625, 0.25),
          Shapes.box(0.125, 0.4375, 0.125, 0.25, 0.5625, 0.875),
          Shapes.box(0.75, 0.4375, 0.125, 0.875, 0.5625, 0.875));

  public WetClayPotBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  protected RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ClayPotBlockEntity(pos, state);
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  protected VoxelShape getCollisionShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    if (level.isClientSide) {
      return null;
    }

    return (lvl, pos, blockState, blockEntity) -> {
      if (blockEntity instanceof ClayPotBlockEntity pot && lvl instanceof ServerLevel serverLevel) {
        tickWet(serverLevel, pos, blockState, pot);
      }
    };
  }

  private static void tickWet(
      ServerLevel level, BlockPos pos, BlockState state, ClayPotBlockEntity pot) {
    boolean heated = isHeated(level, pos);

    if (pot.tickDryingFinished(level, pos, heated)) {
      spawnDryingParticles(level, pos, state);
      playExtinguishSound(level, pos);
      level.setBlock(pos, ModBlocks.CLAY_POT.get().defaultBlockState(), 3);
    }
  }

  private static boolean isHeated(LevelReader level, BlockPos pos) {
    BlockState below = level.getBlockState(pos.below());

    if (below.is(Blocks.CAMPFIRE) && below.getValue(CampfireBlock.LIT)) {
      return true;
    }

    if (below.is(Blocks.SOUL_CAMPFIRE) && below.getValue(CampfireBlock.LIT)) {
      return true;
    }

    return below.is(Blocks.FIRE) || below.is(Blocks.SOUL_FIRE);
  }

  private static void playExtinguishSound(ServerLevel level, BlockPos pos) {
    RandomSource random = level.getRandom();
    float pitch = 0.9F + random.nextFloat() * 0.2F;
    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.7F, pitch);
  }

  private static void spawnDryingParticles(ServerLevel level, BlockPos pos, BlockState wetState) {
    double centerX = pos.getX() + 0.5D;
    double centerY = pos.getY() + 0.25D;
    double centerZ = pos.getZ() + 0.5D;

    level.sendParticles(
        ParticleTypes.CLOUD, centerX, centerY + 0.2D, centerZ, 10, 0.25D, 0.05D, 0.25D, 0.01D);

    level.sendParticles(
        new BlockParticleOption(ParticleTypes.BLOCK, wetState),
        centerX,
        centerY,
        centerZ,
        18,
        0.25D,
        0.10D,
        0.25D,
        0.15D);
  }
}
