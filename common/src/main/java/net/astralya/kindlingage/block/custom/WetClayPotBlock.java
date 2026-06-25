package net.astralya.kindlingage.block.custom;

import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.block.entity.custom.ClayPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
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
  private static final VoxelShape SHAPE =
      Shapes.or(
          Shapes.box(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
          Shapes.box(0.75D, 0.0625D, 0.1875D, 0.8125D, 0.4375D, 0.8125D),
          Shapes.box(0.25D, 0.0625D, 0.1875D, 0.75D, 0.4375D, 0.25D),
          Shapes.box(0.25D, 0.0625D, 0.75D, 0.75D, 0.4375D, 0.8125D),
          Shapes.box(0.1875D, 0.0625D, 0.1875D, 0.25D, 0.4375D, 0.8125D),
          Shapes.box(0.25D, 0.4375D, 0.75D, 0.75D, 0.5625D, 0.875D),
          Shapes.box(0.25D, 0.4375D, 0.125D, 0.75D, 0.5625D, 0.25D),
          Shapes.box(0.125D, 0.4375D, 0.125D, 0.25D, 0.5625D, 0.875D),
          Shapes.box(0.75D, 0.4375D, 0.125D, 0.875D, 0.5625D, 0.875D));

  public WetClayPotBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ClayPotBlockEntity(pos, state);
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public VoxelShape getCollisionShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> blockEntityType) {
    if (level.isClientSide) {
      return null;
    }

    return (tickerLevel, pos, blockState, blockEntity) -> {
      if (blockEntity instanceof ClayPotBlockEntity pot && tickerLevel instanceof ServerLevel serverLevel) {
        tickWet(serverLevel, pos, pot);
      }
    };
  }

  private static void tickWet(ServerLevel level, BlockPos pos, ClayPotBlockEntity pot) {
    if (pot.tickDryingFinished(level, pos, isHeated(level, pos))) {
      level.setBlock(pos, ModBlocks.CLAY_POT.get().defaultBlockState(), Block.UPDATE_ALL);
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
}
