package net.astralya.kindlingage.item.custom;

import net.astralya.kindlingage.block.entity.custom.ClayPotBlockEntity;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class ReedScoopItem extends Item {
  private static final int SCOOP_MB = 1000;

  private final boolean filled;

  public ReedScoopItem(Properties properties, boolean filled) {
    super(properties);
    this.filled = filled;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (filled) {
      return InteractionResultHolder.pass(stack);
    }

    BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
    if (hit.getType() != HitResult.Type.BLOCK) {
      return InteractionResultHolder.pass(stack);
    }

    BlockPos pos = hit.getBlockPos();
    if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hit.getDirection(), stack)) {
      return InteractionResultHolder.fail(stack);
    }

    if (!level.isClientSide) {
      if (tryFillFromClayPot(level, pos, player, hand, stack)) {
        playWoodFillSound(level, pos, player);
        return InteractionResultHolder.success(player.getItemInHand(hand));
      }

      BlockState state = level.getBlockState(pos);
      if (!state.getFluidState().is(Fluids.WATER) || !state.getFluidState().isSource()) {
        return InteractionResultHolder.pass(stack);
      }

      level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
      setFilledInHand(player, hand, stack);
      playWoodFillSound(level, pos, player);
      return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    return InteractionResultHolder.sidedSuccess(stack, true);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    if (!filled) {
      return InteractionResult.PASS;
    }

    Level level = context.getLevel();
    Player player = context.getPlayer();
    InteractionHand hand = context.getHand();
    ItemStack stack = context.getItemInHand();
    BlockPos clickedPos = context.getClickedPos();
    Direction face = context.getClickedFace();

    if (player != null
        && (!level.mayInteract(player, clickedPos)
            || !player.mayUseItemAt(clickedPos, face, stack))) {
      return InteractionResult.FAIL;
    }

    if (!level.isClientSide && tryEmptyIntoClayPot(level, clickedPos)) {
      playWoodEmptySound(level, clickedPos, player);
      if (player != null) {
        consumeFilledScoop(player, hand);
      }
      return InteractionResult.SUCCESS;
    }

    if (level.isClientSide && level.getBlockEntity(clickedPos) instanceof ClayPotBlockEntity) {
      playWoodEmptySound(level, clickedPos, player);
      return InteractionResult.SUCCESS;
    }

    BlockPos placePos = clickedPos.relative(face);
    if (player != null
        && (!level.mayInteract(player, placePos) || !player.mayUseItemAt(placePos, face, stack))) {
      return InteractionResult.FAIL;
    }

    BlockState placeState = level.getBlockState(placePos);
    boolean placed;
    if (level.isClientSide) {
      placed =
          placeState.canBeReplaced(Fluids.WATER)
              || placeState.getBlock() instanceof LiquidBlockContainer;
      if (placed) {
        playWoodEmptySound(level, placePos, player);
      }
    } else if (placeState.canBeReplaced(Fluids.WATER)) {
      placed = level.setBlock(placePos, Fluids.WATER.defaultFluidState().createLegacyBlock(), 3);
    } else if (placeState.getBlock() instanceof LiquidBlockContainer container) {
      placed = container.placeLiquid(level, placePos, placeState, Fluids.WATER.defaultFluidState());
    } else {
      placed = false;
    }

    if (placed && !level.isClientSide) {
      playWoodEmptySound(level, placePos, player);
      if (player != null) {
        consumeFilledScoop(player, hand);
      }
    }

    return placed ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.FAIL;
  }

  private static boolean tryFillFromClayPot(
      Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack scoop) {
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof ClayPotBlockEntity pot) || pot.drainWater(SCOOP_MB) != SCOOP_MB) {
      return false;
    }

    setFilledInHand(player, hand, scoop);
    return true;
  }

  private static boolean tryEmptyIntoClayPot(Level level, BlockPos pos) {
    BlockEntity blockEntity = level.getBlockEntity(pos);
    return blockEntity instanceof ClayPotBlockEntity pot && pot.fillWater(SCOOP_MB) == SCOOP_MB;
  }

  private static void setFilledInHand(Player player, InteractionHand hand, ItemStack current) {
    if (player.getAbilities().instabuild) {
      return;
    }

    ItemStack filledStack = new ItemStack(ModItems.WATER_REED_SCOOP.get());
    filledStack.setDamageValue(current.getDamageValue());
    player.setItemInHand(hand, filledStack);
  }

  private static void consumeFilledScoop(Player player, InteractionHand hand) {
    if (!player.getAbilities().instabuild) {
      player.setItemInHand(hand, ItemStack.EMPTY);
    }
  }

  private static void playWoodFillSound(Level level, BlockPos pos, Player player) {
    level.playSound(player, pos, SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 0.9F, 1.05F);
    level.playSound(player, pos, SoundEvents.BAMBOO_HIT, SoundSource.PLAYERS, 0.6F, 1.2F);
  }

  private static void playWoodEmptySound(Level level, BlockPos pos, Player player) {
    level.playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 0.9F, 1.05F);
    level.playSound(player, pos, SoundEvents.BAMBOO_HIT, SoundSource.PLAYERS, 0.6F, 0.95F);
  }
}
