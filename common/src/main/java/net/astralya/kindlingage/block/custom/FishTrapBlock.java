package net.astralya.kindlingage.block.custom;

import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.block.entity.custom.FishTrapBlockEntity;
import net.astralya.kindlingage.item.ModItems;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FishTrapBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

  private static final VoxelShape CLOSED_SHAPE = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
  private static final VoxelShape OPEN_SHAPE =
      Shapes.or(
          Shapes.box(0.125D, 0.0D, 0.125D, 0.875D, 0.125D, 0.875D),
          Shapes.box(0.125D, 0.0D, 0.0D, 0.875D, 0.125D, 0.125D),
          Shapes.box(0.125D, 0.375D, 0.0D, 0.875D, 0.5D, 0.125D),
          Shapes.box(0.125D, 0.0D, 0.875D, 0.875D, 0.125D, 1.0D),
          Shapes.box(0.125D, 0.375D, 0.875D, 0.875D, 0.5D, 1.0D),
          Shapes.box(0.0D, 0.0D, 0.125D, 0.125D, 0.125D, 0.875D),
          Shapes.box(0.0D, 0.375D, 0.125D, 0.125D, 0.5D, 0.875D),
          Shapes.box(0.875D, 0.0D, 0.125D, 1.0D, 0.125D, 0.875D),
          Shapes.box(0.875D, 0.375D, 0.125D, 1.0D, 0.5D, 0.875D),
          Shapes.box(0.0D, 0.0D, 0.0D, 0.125D, 0.5D, 0.125D),
          Shapes.box(0.875D, 0.0D, 0.0D, 1.0D, 0.5D, 0.125D),
          Shapes.box(0.0D, 0.0D, 0.875D, 0.125D, 0.5D, 1.0D),
          Shapes.box(0.875D, 0.0D, 0.875D, 1.0D, 0.5D, 1.0D));

  public FishTrapBlock(Properties properties) {
    super(properties);
    registerDefaultState(
        stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, false)
            .setValue(OPEN, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, WATERLOGGED, OPEN);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
    return defaultBlockState()
        .setValue(FACING, context.getHorizontalDirection().getOpposite())
        .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
        .setValue(OPEN, false);
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new FishTrapBlockEntity(pos, state);
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return state.getValue(OPEN) ? OPEN_SHAPE : CLOSED_SHAPE;
  }

  @Override
  public VoxelShape getCollisionShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return state.getValue(OPEN) ? OPEN_SHAPE : CLOSED_SHAPE;
  }

  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  public BlockState updateShape(
      BlockState state,
      Direction direction,
      BlockState neighborState,
      LevelAccessor level,
      BlockPos pos,
      BlockPos neighborPos) {
    if (state.getValue(WATERLOGGED)) {
      level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }

    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
  }

  @Override
  public InteractionResult use(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    if (player.isShiftKeyDown()) {
      if (level.isClientSide) {
        return InteractionResult.SUCCESS;
      }

      boolean open = !state.getValue(OPEN);
      level.setBlock(pos, state.setValue(OPEN, open), Block.UPDATE_ALL);
      level.playSound(
          null,
          pos,
          open ? SoundEvents.WOODEN_TRAPDOOR_OPEN : SoundEvents.WOODEN_TRAPDOOR_CLOSE,
          SoundSource.BLOCKS,
          0.8F,
          1.0F);
      return InteractionResult.CONSUME;
    }

    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof FishTrapBlockEntity fishTrap)) {
      return InteractionResult.PASS;
    }

    ItemStack held = player.getItemInHand(hand);
    if (held.is(ModItems.WILD_SEEDS.get())) {
      if (level.isClientSide) {
        return fishTrap.getBaitCount() < FishTrapBlockEntity.MAX_BAIT
            ? InteractionResult.SUCCESS
            : InteractionResult.PASS;
      }

      if (fishTrap.insertSeed(player, held)) {
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.5F, 1.0F);
        return InteractionResult.CONSUME;
      }
      return InteractionResult.PASS;
    }

    if (level.isClientSide) {
      return fishTrap.getCatchCount() > 0 || fishTrap.getBaitCount() > 0
          ? InteractionResult.SUCCESS
          : InteractionResult.PASS;
    }

    if (fishTrap.extractFish(player) || fishTrap.extractSeed(player)) {
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
      if (blockEntity instanceof FishTrapBlockEntity fishTrap) {
        fishTrap.dropContents(level, pos);
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
        type, ModBlockEntityTypes.FISH_TRAP.get(), FishTrapBlockEntity::serverTick);
  }
}
