package net.astralya.kindlingage.block.custom;

import net.minecraft.core.BlockPos;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.block.entity.custom.HearthPitBlockEntity;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class HearthPitBlock extends BaseEntityBlock {
  public static final BooleanProperty LIT = BlockStateProperties.LIT;

  private static final VoxelShape SHAPE =
      Shapes.or(
          Shapes.box(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
          Shapes.box(0.1875D, 0.0D, 0.0D, 0.8125D, 0.3125D, 0.1875D),
          Shapes.box(0.1875D, 0.0D, 0.8125D, 0.8125D, 0.3125D, 1.0D),
          Shapes.box(0.0D, 0.0D, 0.1875D, 0.1875D, 0.3125D, 0.8125D),
          Shapes.box(0.8125D, 0.0D, 0.1875D, 1.0D, 0.3125D, 0.8125D),
          Shapes.box(0.0D, 0.0D, 0.0D, 0.1875D, 0.1875D, 0.1875D),
          Shapes.box(0.8125D, 0.0D, 0.0D, 1.0D, 0.4375D, 0.1875D),
          Shapes.box(0.0D, 0.0D, 0.8125D, 0.1875D, 0.25D, 1.0D),
          Shapes.box(0.8125D, 0.0D, 0.8125D, 1.0D, 0.1875D, 1.0D));

  public HearthPitBlock(Properties properties) {
    super(properties);
    registerDefaultState(stateDefinition.any().setValue(LIT, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(LIT);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(LIT, false);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new HearthPitBlockEntity(pos, state);
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
  public InteractionResult use(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    ItemStack held = player.getItemInHand(hand);

    if (held.is(ModItems.KINDLING.get())) {
      if (level.isClientSide) {
        return InteractionResult.SUCCESS;
      }

      if (!state.getValue(LIT)) {
        level.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_ALL);
      }
      if (!player.getAbilities().instabuild) {
        held.shrink(1);
      }
      level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 0.8F, 1.0F);
      return InteractionResult.CONSUME;
    }

    if (held.is(Items.WATER_BUCKET) && state.getValue(LIT)) {
      if (level.isClientSide) {
        return InteractionResult.SUCCESS;
      }

      level.setBlock(pos, state.setValue(LIT, false), Block.UPDATE_ALL);
      if (!player.getAbilities().instabuild) {
        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
      }
      level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.8F, 1.0F);
      return InteractionResult.CONSUME;
    }

    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof HearthPitBlockEntity hearthPit)) {
      return InteractionResult.PASS;
    }

    CampfireCookingRecipe recipe = hearthPit.getCookableRecipe(held);
    if (!held.isEmpty() && recipe != null) {
      if (level.isClientSide) {
        return InteractionResult.SUCCESS;
      }

      int cookTime = HearthPitBlockEntity.adjustedCookTime(recipe.getCookingTime());
      if (hearthPit.insertItem(player, held, cookTime)) {
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.45F, 1.0F);
        return InteractionResult.CONSUME;
      }
    }

    if (level.isClientSide) {
      return hearthPit.canExtract() ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    if (hearthPit.extractItem(player)) {
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
      if (blockEntity instanceof HearthPitBlockEntity hearthPit) {
        hearthPit.dropContents(level, pos);
      }
    }

    super.onRemove(state, level, pos, newState, movedByPiston);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    if (level.isClientSide) {
      return createTickerHelper(
          type, ModBlockEntityTypes.HEARTH_PIT.get(), HearthPitBlockEntity::clientTick);
    }

    return createTickerHelper(
        type, ModBlockEntityTypes.HEARTH_PIT.get(), HearthPitBlockEntity::serverTick);
  }
}
