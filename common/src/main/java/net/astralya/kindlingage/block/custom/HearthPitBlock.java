package net.astralya.kindlingage.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.block.entity.custom.HearthPitBlockEntity;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class HearthPitBlock extends CampfireBlock {
  @SuppressWarnings("unchecked")
  public static final MapCodec<HearthPitBlock> CODEC = simpleCodec(HearthPitBlock::new);
  private static final VoxelShape SHAPE = makeShape();

  public static VoxelShape makeShape() {
    return Shapes.or(
        Shapes.box(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
        Shapes.box(0.1875D, 0.0D, 0.0D, 0.8125D, 0.3125D, 0.1875D),
        Shapes.box(0.1875D, 0.0D, 0.8125D, 0.8125D, 0.3125D, 1.0D),
        Shapes.box(0.0D, 0.0D, 0.1875D, 0.1875D, 0.3125D, 0.8125D),
        Shapes.box(0.8125D, 0.0D, 0.1875D, 1.0D, 0.3125D, 0.8125D),
        Shapes.box(0.0D, 0.0D, 0.0D, 0.1875D, 0.1875D, 0.1875D),
        Shapes.box(0.8125D, 0.0D, 0.0D, 1.0D, 0.4375D, 0.1875D),
        Shapes.box(0.0D, 0.0D, 0.8125D, 0.1875D, 0.25D, 1.0D),
        Shapes.box(0.8125D, 0.0D, 0.8125D, 1.0D, 0.1875D, 1.0D));
  }

  public HearthPitBlock(Properties properties) {
    super(true, 1, properties);
  }

  @Override
  @SuppressWarnings("unchecked")
  public MapCodec<CampfireBlock> codec() {
    return (MapCodec<CampfireBlock>) (MapCodec<?>) CODEC;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return super.getStateForPlacement(context).setValue(LIT, false);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new HearthPitBlockEntity(pos, state);
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
  protected ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof HearthPitBlockEntity hearthPit)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    if (stack.is(ModItems.KINDLING.get())) {
      if (level.isClientSide) {
        return ItemInteractionResult.sidedSuccess(true);
      }

      if (state.getValue(WATERLOGGED)) {
        return ItemInteractionResult.CONSUME;
      }

      if (hearthPit.addKindling(player, stack)) {
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 0.8F, 1.0F);
        player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
      }

      return ItemInteractionResult.CONSUME;
    }

    if (!(player instanceof LivingEntity livingEntity)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    java.util.Optional<RecipeHolder<CampfireCookingRecipe>> recipe =
        hearthPit.getCookableRecipe(stack);
    if (recipe.isEmpty()) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    if (level.isClientSide) {
      return ItemInteractionResult.CONSUME;
    }

    int cookTime = HearthPitBlockEntity.adjustedCookTime(recipe.get().value().getCookingTime());
    if (hearthPit.placeFood(livingEntity, stack, cookTime)) {
      player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
      return ItemInteractionResult.SUCCESS;
    }

    return ItemInteractionResult.CONSUME;
  }

  @Override
  public boolean placeLiquid(
      LevelAccessor level,
      BlockPos pos,
      BlockState state,
      net.minecraft.world.level.material.FluidState fluidState) {
    boolean placed = super.placeLiquid(level, pos, state, fluidState);
    if (placed) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof HearthPitBlockEntity hearthPit) {
        hearthPit.extinguish();
      }
    }
    return placed;
  }

  @Override
  protected void onProjectileHit(
      Level level, BlockState state, BlockHitResult hit, Projectile projectile) {}

  @Override
  protected void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (!state.is(newState.getBlock())) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof HearthPitBlockEntity hearthPit) {
        Containers.dropContents(level, pos, hearthPit.getItems());
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
