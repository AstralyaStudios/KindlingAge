package net.astralya.kindlingage.block.custom;

import net.astralya.kindlingage.block.entity.custom.WickerBasketBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class WickerBasketBlock extends BaseEntityBlock {
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

  private static final VoxelShape SHAPE =
      Shapes.or(
          Shapes.box(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.125D, 0.9375D),
          Shapes.box(0.0625D, 0.4375D, 0.0625D, 0.9375D, 0.5D, 0.9375D),
          Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 0.0625D),
          Shapes.box(0.5625D, 0.3125D, 0.0D, 1.0D, 0.4375D, 0.0625D),
          Shapes.box(0.0D, 0.3125D, 0.0D, 0.4375D, 0.4375D, 0.0625D),
          Shapes.box(0.0D, 0.4375D, 0.0D, 1.0D, 0.5D, 0.0625D),
          Shapes.box(0.0D, 0.0D, 0.9375D, 1.0D, 0.3125D, 1.0D),
          Shapes.box(0.5625D, 0.3125D, 0.9375D, 1.0D, 0.4375D, 1.0D),
          Shapes.box(0.0D, 0.3125D, 0.9375D, 0.4375D, 0.4375D, 1.0D),
          Shapes.box(0.0D, 0.4375D, 0.9375D, 1.0D, 0.5D, 1.0D),
          Shapes.box(0.0D, 0.0D, 0.0625D, 0.0625D, 0.3125D, 0.9375D),
          Shapes.box(0.0D, 0.3125D, 0.0625D, 0.0625D, 0.4375D, 0.4375D),
          Shapes.box(0.0D, 0.3125D, 0.5625D, 0.0625D, 0.4375D, 0.9375D),
          Shapes.box(0.0D, 0.4375D, 0.0625D, 0.0625D, 0.5D, 0.9375D),
          Shapes.box(0.9375D, 0.0D, 0.0625D, 1.0D, 0.3125D, 0.9375D),
          Shapes.box(0.9375D, 0.3125D, 0.0625D, 1.0D, 0.4375D, 0.4375D),
          Shapes.box(0.9375D, 0.3125D, 0.5625D, 1.0D, 0.4375D, 0.9375D),
          Shapes.box(0.9375D, 0.4375D, 0.0625D, 1.0D, 0.5D, 0.9375D));

  public WickerBasketBlock(BlockBehaviour.Properties properties) {
    super(properties);
    registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new WickerBasketBlockEntity(pos, state);
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
  public InteractionResult use(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      net.minecraft.world.InteractionHand hand,
      BlockHitResult hit) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (blockEntity instanceof WickerBasketBlockEntity basket) {
      player.openMenu(basket);
      return InteractionResult.CONSUME;
    }

    return InteractionResult.PASS;
  }

  @Override
  public void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (!state.is(newState.getBlock())) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof WickerBasketBlockEntity basket) {
        Containers.dropContents(level, pos, basket);
      }
    }

    super.onRemove(state, level, pos, newState, movedByPiston);
  }
}
