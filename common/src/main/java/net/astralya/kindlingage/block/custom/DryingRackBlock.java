package net.astralya.kindlingage.block.custom;

import java.util.EnumMap;
import java.util.Map;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.block.entity.custom.DryingRackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class DryingRackBlock extends BaseEntityBlock {
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

  private static final VoxelShape NORTH_SHAPE =
      Shapes.or(
          Shapes.box(0.0625D, 0.0D, 0.4375D, 0.1875D, 0.875D, 0.5625D),
          Shapes.box(0.8125D, 0.0D, 0.4375D, 0.9375D, 0.875D, 0.5625D),
          Shapes.box(0.0625D, 0.875D, 0.4375D, 0.9375D, 1.0D, 0.5625D),
          Shapes.box(0.1875D, 0.5D, 0.4375D, 0.8125D, 0.5625D, 0.5625D),
          Shapes.box(0.1875D, 0.125D, 0.4375D, 0.8125D, 0.1875D, 0.5625D),
          Shapes.box(0.34375D, 0.1875D, 0.46875D, 0.40625D, 0.875D, 0.53125D),
          Shapes.box(0.578125D, 0.1875D, 0.46875D, 0.640625D, 0.875D, 0.53125D));

  private static final Map<Direction, VoxelShape> SHAPES = makeShapes();

  public DryingRackBlock(Properties properties) {
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
    return new DryingRackBlockEntity(pos, state);
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPES.getOrDefault(state.getValue(FACING), NORTH_SHAPE);
  }

  @Override
  public VoxelShape getCollisionShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPES.getOrDefault(state.getValue(FACING), NORTH_SHAPE);
  }

  @Override
  public InteractionResult use(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof DryingRackBlockEntity dryingRack)) {
      return InteractionResult.PASS;
    }

    ItemStack held = player.getItemInHand(hand);

    if (!held.isEmpty() && dryingRack.canAcceptItem(held)) {
      if (level.isClientSide) {
        return InteractionResult.SUCCESS;
      }

      if (dryingRack.insertItem(player, held)) {
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.45F, 0.95F);
        return InteractionResult.CONSUME;
      }
    }

    if (level.isClientSide) {
      return dryingRack.canExtractAny() ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    if (dryingRack.extractNextItem(player)) {
      level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.3F, 1.0F);
      return InteractionResult.CONSUME;
    }

    return InteractionResult.PASS;
  }

  @Override
  public void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (!state.is(newState.getBlock())) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof DryingRackBlockEntity dryingRack) {
        dryingRack.dropContents(level, pos);
      }
    }

    super.onRemove(state, level, pos, newState, movedByPiston);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    if (level.isClientSide) {
      return null;
    }

    return createTickerHelper(
        type, ModBlockEntityTypes.DRYING_RACK.get(), DryingRackBlockEntity::serverTick);
  }

  private static Map<Direction, VoxelShape> makeShapes() {
    Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
    shapes.put(Direction.NORTH, NORTH_SHAPE);
    shapes.put(Direction.EAST, rotateShape(NORTH_SHAPE, Direction.EAST));
    shapes.put(Direction.SOUTH, rotateShape(NORTH_SHAPE, Direction.SOUTH));
    shapes.put(Direction.WEST, rotateShape(NORTH_SHAPE, Direction.WEST));
    return shapes;
  }

  private static VoxelShape rotateShape(VoxelShape shape, Direction facing) {
    VoxelShape rotated = shape;
    for (int i = 0; i < quarterTurnsFromNorth(facing); i++) {
      rotated = rotateClockwise(rotated);
    }
    return rotated;
  }

  private static int quarterTurnsFromNorth(Direction facing) {
    return switch (facing) {
      case NORTH -> 0;
      case EAST -> 1;
      case SOUTH -> 2;
      case WEST -> 3;
      default -> 0;
    };
  }

  private static VoxelShape rotateClockwise(VoxelShape shape) {
    VoxelShape rotated = Shapes.empty();
    for (AABB box : shape.toAabbs()) {
      rotated =
          Shapes.or(
              rotated,
              Shapes.box(
                  1.0D - box.maxZ, box.minY, box.minX, 1.0D - box.minZ, box.maxY, box.maxX));
    }
    return rotated;
  }
}
