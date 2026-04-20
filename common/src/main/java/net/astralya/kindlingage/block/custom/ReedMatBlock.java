package net.astralya.kindlingage.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ReedMatBlock extends Block {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;

  private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

  public ReedMatBlock(Properties properties) {
    super(properties);
    registerDefaultState(
        stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, BedPart.FOOT));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, PART);
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
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
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    BlockPos below = pos.below();
    return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction facing = context.getHorizontalDirection();
    BlockPos pos = context.getClickedPos();
    Level level = context.getLevel();

    BlockPos headPos = pos.relative(facing);
    if (!level.getWorldBorder().isWithinBounds(headPos)) {
      return null;
    }

    if (!level.getBlockState(headPos).canBeReplaced(context)) {
      return null;
    }

    BlockState footState =
        defaultBlockState().setValue(FACING, facing).setValue(PART, BedPart.FOOT);

    if (!footState.canSurvive(level, pos)) {
      return null;
    }

    BlockState headState = footState.setValue(PART, BedPart.HEAD);
    if (!headState.canSurvive(level, headPos)) {
      return null;
    }

    return footState;
  }

  @Override
  public void setPlacedBy(
      Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    if (level.isClientSide) {
      return;
    }

    Direction facing = state.getValue(FACING);
    BlockPos headPos = pos.relative(facing);
    BlockState headState = state.setValue(PART, BedPart.HEAD);

    level.setBlock(headPos, headState, Block.UPDATE_ALL);
    level.updateNeighborsAt(pos, this);
    level.updateNeighborsAt(headPos, this);
  }

  @Override
  protected BlockState updateShape(
      BlockState state,
      Direction direction,
      BlockState neighborState,
      LevelAccessor level,
      BlockPos pos,
      BlockPos neighborPos) {
    BedPart part = state.getValue(PART);
    Direction facing = state.getValue(FACING);

    Direction otherDirection = part == BedPart.FOOT ? facing : facing.getOpposite();
    if (direction == otherDirection) {
      if (!neighborState.is(this) || neighborState.getValue(PART) == part) {
        return Blocks.AIR.defaultBlockState();
      }
    }

    if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
      return Blocks.AIR.defaultBlockState();
    }

    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
  }

  @Override
  public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    if (!level.isClientSide) {
      BedPart part = state.getValue(PART);
      Direction facing = state.getValue(FACING);

      BlockPos otherPos =
          part == BedPart.FOOT ? pos.relative(facing) : pos.relative(facing.getOpposite());
      BlockState otherState = level.getBlockState(otherPos);

      if (otherState.is(this) && otherState.getValue(PART) != part) {
        level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.updateNeighborsAt(otherPos, this);
      }
    }

    return super.playerWillDestroy(level, pos, state, player);
  }

  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    BlockPos footPos = getFootPos(state, pos);
    if (player instanceof ServerPlayer serverPlayer) {
      trySetRespawn(serverPlayer, level, footPos);
    }

    return InteractionResult.CONSUME;
  }

  @Override
  protected ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      net.minecraft.world.InteractionHand hand,
      BlockHitResult hit) {
    if (level.isClientSide) {
      return ItemInteractionResult.SUCCESS;
    }

    BlockPos footPos = getFootPos(state, pos);
    if (player instanceof ServerPlayer serverPlayer) {
      trySetRespawn(serverPlayer, level, footPos);
    }

    return ItemInteractionResult.CONSUME;
  }

  private static BlockPos getFootPos(BlockState state, BlockPos pos) {
    return state.getValue(PART) == BedPart.FOOT
        ? pos
        : pos.relative(state.getValue(FACING).getOpposite());
  }

  private static void trySetRespawn(ServerPlayer player, Level level, BlockPos pos) {
    BlockPos respawnPos = pos.above();
    player.setRespawnPosition(level.dimension(), respawnPos, player.getYRot(), true, true);
  }
}
