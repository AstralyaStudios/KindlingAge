package net.astralya.kindlingage.block.custom;

import net.astralya.kindlingage.block.entity.custom.ClayPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ClayPotBlock extends BaseEntityBlock {
  private static final int BUCKET_MB = 1000;
  private static final int BOTTLE_MB = 250;

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

  public ClayPotBlock(Properties properties) {
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
  public InteractionResult use(
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
      return InteractionResult.PASS;
    }

    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof ClayPotBlockEntity pot)) {
      return InteractionResult.CONSUME;
    }

    if (waterBucket) {
      if (pot.fillWater(BUCKET_MB) == BUCKET_MB) {
        if (!player.getAbilities().instabuild) {
          player.setItemInHand(hand, new ItemStack(Items.BUCKET));
        }

        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      return InteractionResult.CONSUME;
    }

    if (emptyBucket) {
      if (pot.drainWater(BUCKET_MB) == BUCKET_MB) {
        if (!player.getAbilities().instabuild) {
          player.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
        }

        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      return InteractionResult.CONSUME;
    }

    if (pot.drainWater(BOTTLE_MB) == BOTTLE_MB) {
      if (!player.getAbilities().instabuild) {
        held.shrink(1);
        ItemStack waterBottle = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);

        if (!player.getInventory().add(waterBottle)) {
          player.drop(waterBottle, false);
        }
      }

      level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    return InteractionResult.CONSUME;
  }
}
