package net.astralya.kindlingage.block.custom;

import net.astralya.kindlingage.KindlingAgeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ReedMatBlock extends BedBlock {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
  public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

  private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

  public ReedMatBlock(Properties properties) {
    super(DyeColor.BROWN, properties);
    registerDefaultState(
        stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(PART, BedPart.FOOT)
            .setValue(OCCUPIED, false));
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return null;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, PART, OCCUPIED);
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
  public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
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
        defaultBlockState()
            .setValue(FACING, facing)
            .setValue(PART, BedPart.FOOT)
            .setValue(OCCUPIED, false);

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
  public BlockState updateShape(
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
  public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
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

    super.playerWillDestroy(level, pos, state, player);
  }

  @Override
  public InteractionResult use(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    BlockPos footPos = getFootPos(state, pos);
    if (player instanceof ServerPlayer serverPlayer) {
      if (KindlingAgeConfig.reedMatAllowsSleeping()) {
        trySleep(serverPlayer, level, footPos, level.getBlockState(footPos));
        return InteractionResult.CONSUME;
      }

      trySetRespawn(serverPlayer, level, footPos);
    }

    return InteractionResult.CONSUME;
  }

  @Override
  public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    BlockPos footPos = getFootPos(state, pos);
    if (!KindlingAgeConfig.reedMatAllowsSleeping()) {
      return;
    }

    if (!level.isDay()) {
      if (hasSleeperAt(level, footPos)) {
        level.scheduleTick(footPos, this, 20);
        return;
      }

      clearOccupied(level, footPos);
      if (state.getValue(PART) == BedPart.HEAD) {
        clearOccupied(level, pos);
      }
      return;
    }

    if (hasSleeperAt(level, footPos)) {
      level.scheduleTick(footPos, this, 20);
      return;
    }

    consumeMat(level, footPos);
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

  private void trySleep(ServerPlayer player, Level level, BlockPos footPos, BlockState footState) {
    if (footState.getValue(OCCUPIED) && !hasSleeperAt((ServerLevel) level, footPos)) {
      clearOccupied((ServerLevel) level, footPos);
      footState = level.getBlockState(footPos);
    }

    if (footState.getValue(OCCUPIED)) {
      player.displayClientMessage(
          net.minecraft.network.chat.Component.translatable("block.minecraft.bed.occupied"), true);
      return;
    }

    player
        .startSleepInBed(footPos)
        .ifLeft(problem -> player.displayClientMessage(problem.getMessage(), true));
    if (player.isSleeping() && level instanceof ServerLevel serverLevel) {
      serverLevel.setBlock(footPos, footState.setValue(OCCUPIED, true), Block.UPDATE_ALL);
      setHeadOccupied(serverLevel, footPos, footState, true);
      serverLevel.updateSleepingPlayerList();
      serverLevel.scheduleTick(footPos, this, 20);
    }
  }

  private static boolean hasSleeperAt(ServerLevel level, BlockPos footPos) {
    return level.players().stream()
        .anyMatch(
            player ->
                player.isSleeping()
                    && player.getSleepingPos().map(footPos::equals).orElse(false));
  }

  private static void consumeMat(ServerLevel level, BlockPos footPos) {
    BlockState footState = level.getBlockState(footPos);
    if (!(footState.getBlock() instanceof ReedMatBlock)) {
      return;
    }

    Direction facing = footState.getValue(FACING);
    BlockPos headPos = footPos.relative(facing);
    BlockState headState = level.getBlockState(headPos);
    if (headState.is(footState.getBlock()) && headState.getValue(PART) == BedPart.HEAD) {
      level.removeBlock(headPos, false);
    }
    level.removeBlock(footPos, false);
  }

  private static void clearOccupied(ServerLevel level, BlockPos footPos) {
    BlockState footState = level.getBlockState(footPos);
    if (footState.getBlock() instanceof ReedMatBlock && footState.getValue(OCCUPIED)) {
      level.setBlock(footPos, footState.setValue(OCCUPIED, false), Block.UPDATE_ALL);
      setHeadOccupied(level, footPos, footState, false);
      level.updateSleepingPlayerList();
    }
  }

  private static void setHeadOccupied(
      ServerLevel level, BlockPos footPos, BlockState footState, boolean occupied) {
    BlockPos headPos = footPos.relative(footState.getValue(FACING));
    BlockState headState = level.getBlockState(headPos);
    if (headState.is(footState.getBlock()) && headState.getValue(PART) == BedPart.HEAD) {
      level.setBlock(headPos, headState.setValue(OCCUPIED, occupied), Block.UPDATE_ALL);
    }
  }
}
