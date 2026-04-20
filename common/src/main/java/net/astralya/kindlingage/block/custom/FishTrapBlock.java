package net.astralya.kindlingage.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.block.entity.custom.FishTrapBlockEntity;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
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
  public static final MapCodec<FishTrapBlock> CODEC = simpleCodec(FishTrapBlock::new);
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  private static final VoxelShape SHAPE = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

  public FishTrapBlock(Properties properties) {
    super(properties);
    registerDefaultState(
        stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  protected void createBlockStateDefinition(
      StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
    builder.add(FACING, WATERLOGGED);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
    return defaultBlockState()
        .setValue(FACING, context.getHorizontalDirection().getOpposite())
        .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
  }

  @Override
  protected BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  protected RenderShape getRenderShape(BlockState state) {
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
  protected FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  protected BlockState updateShape(
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
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new FishTrapBlockEntity(pos, state);
  }

  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof FishTrapBlockEntity fishTrap)) {
      return InteractionResult.PASS;
    }

    if (player.isShiftKeyDown()) {
      if (!level.isClientSide) {
        player.displayClientMessage(fishTrap.getDebugMessage(hit, state), false);
      }
      return InteractionResult.sidedSuccess(level.isClientSide);
    }

    FishTrapBlockEntity.InteractionZone zone = fishTrap.getInteractionZone(hit, state);
    if (zone == FishTrapBlockEntity.InteractionZone.SEED) {
      if (level.isClientSide) {
        return fishTrap.getBaitCount() > 0 ? InteractionResult.SUCCESS : InteractionResult.PASS;
      }

      if (fishTrap.extractSeed(player)) {
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.3F, 1.2F);
        return InteractionResult.CONSUME;
      }
      return InteractionResult.PASS;
    }

    if (zone == FishTrapBlockEntity.InteractionZone.FISH) {
      if (level.isClientSide) {
        return fishTrap.getCatchCount() > 0 ? InteractionResult.SUCCESS : InteractionResult.PASS;
      }

      if (fishTrap.extractFish(player)) {
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.3F, 0.9F);
        return InteractionResult.CONSUME;
      }
      return InteractionResult.PASS;
    }

    return InteractionResult.PASS;
  }

  @Override
  protected ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof FishTrapBlockEntity fishTrap)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    if (player.isShiftKeyDown()) {
      if (!level.isClientSide) {
        player.displayClientMessage(fishTrap.getDebugMessage(hit, state), false);
      }
      return ItemInteractionResult.SUCCESS;
    }

    FishTrapBlockEntity.InteractionZone zone = fishTrap.getInteractionZone(hit, state);
    if (zone == FishTrapBlockEntity.InteractionZone.FISH) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    if (zone != FishTrapBlockEntity.InteractionZone.SEED) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    if (!stack.is(ModItems.WILD_SEEDS.get())) {
      return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    if (level.isClientSide) {
      return fishTrap.getBaitCount() < FishTrapBlockEntity.MAX_BAIT
          ? ItemInteractionResult.SUCCESS
          : ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    if (fishTrap.insertSeed(player, stack)) {
      level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.5F, 1.0F);
      return ItemInteractionResult.SUCCESS;
    }
    return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  protected void onRemove(
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
      return createTickerHelper(
          type, ModBlockEntityTypes.FISH_TRAP.get(), FishTrapBlockEntity::clientTick);
    }

    return createTickerHelper(
        type, ModBlockEntityTypes.FISH_TRAP.get(), FishTrapBlockEntity::serverTick);
  }
}
