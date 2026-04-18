package net.astralya.kindlingage.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.kindlingage.block.entity.custom.ClayPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ClayPotBlock extends BaseEntityBlock {
  public static final MapCodec<ClayPotBlock> CODEC = simpleCodec(ClayPotBlock::new);

  private static final int BUCKET_MB = 1000;
  private static final int BOTTLE_MB = 250;

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

  public ClayPotBlock(Properties properties) {
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
  protected ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    ItemStack held = player.getItemInHand(hand);

    boolean waterBucket = held.is(Items.WATER_BUCKET);
    boolean emptyBucket = held.is(Items.BUCKET);
    boolean glassBottle = held.is(Items.GLASS_BOTTLE);

    if (!(waterBucket || emptyBucket || glassBottle)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    if (level.isClientSide) {
      return ItemInteractionResult.sidedSuccess(true);
    }

    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof ClayPotBlockEntity pot)) {
      return ItemInteractionResult.CONSUME;
    }

    if (waterBucket) {
      if (pot.fillWater(BUCKET_MB) == BUCKET_MB) {
        if (!player.getAbilities().instabuild) {
          player.setItemInHand(hand, new ItemStack(Items.BUCKET));
        }

        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      return ItemInteractionResult.CONSUME;
    }

    if (emptyBucket) {
      if (pot.drainWater(BUCKET_MB) == BUCKET_MB) {
        if (!player.getAbilities().instabuild) {
          player.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
        }

        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      return ItemInteractionResult.CONSUME;
    }

    if (pot.drainWater(BOTTLE_MB) == BOTTLE_MB) {
      if (!player.getAbilities().instabuild) {
        held.shrink(1);
        ItemStack waterBottle = createWaterBottle();

        if (!player.getInventory().add(waterBottle)) {
          player.drop(waterBottle, false);
        }
      }

      level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    return ItemInteractionResult.CONSUME;
  }

  private static ItemStack createWaterBottle() {
    ItemStack bottle = new ItemStack(Items.POTION);
    bottle.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.WATER));
    return bottle;
  }
}
