package net.astralya.kindlingage.item.custom;

import java.util.function.Supplier;
import net.astralya.kindlingage.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class FlintKnifeItem extends SwordItem {
  private final Supplier<Item> fiberItem;
  private final int fiberCount;
  private final Supplier<Item> wildSeedItem;
  private final int wildSeedCount;
  private final float wildSeedChance;

  public FlintKnifeItem(
      Tier tier,
      Supplier<Item> fiberItem,
      int fiberCount,
      Supplier<Item> wildSeedItem,
      int wildSeedCount,
      float wildSeedChance,
      Properties properties) {
    super(tier, 1, -2.6F, properties);
    this.fiberItem = fiberItem;
    this.fiberCount = fiberCount;
    this.wildSeedItem = wildSeedItem;
    this.wildSeedCount = wildSeedCount;
    this.wildSeedChance = wildSeedChance;
  }

  @Override
  public boolean mineBlock(
      ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
    if (level.isClientSide || !(entity instanceof Player player)) {
      return super.mineBlock(stack, level, state, pos, entity);
    }

    if (state.is(ModTags.Blocks.FLINT_KNIFE_FIBER_SOURCES)) {
      spawnDrop(level, pos, new ItemStack(fiberItem.get(), fiberCount));
      damageHeldStack(stack, player);
      return true;
    }

    if (state.is(ModTags.Blocks.FLINT_KNIFE_WILD_SEED_SOURCES)) {
      if (level.random.nextFloat() < wildSeedChance) {
        spawnDrop(level, pos, new ItemStack(wildSeedItem.get(), wildSeedCount));
      }
      damageHeldStack(stack, player);
      return true;
    }

    if (state.is(ModTags.Blocks.FLINT_KNIFE_STICK_SOURCES)) {
      spawnDrop(level, pos, new ItemStack(Items.STICK));
      damageHeldStack(stack, player);
      return true;
    }

    return super.mineBlock(stack, level, state, pos, entity);
  }

  @Override
  public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    damageHeldStack(stack, attacker);
    return true;
  }

  @Override
  public boolean isCorrectToolForDrops(BlockState state) {
    return false;
  }

  @Override
  public int getEnchantmentValue() {
    return 10;
  }

  private static void damageHeldStack(ItemStack stack, LivingEntity entity) {
    InteractionHand hand =
        entity.getOffhandItem() == stack ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    EquipmentSlot slot = hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
    stack.hurtAndBreak(1, entity, livingEntity -> livingEntity.broadcastBreakEvent(slot));
  }

  private static void spawnDrop(Level level, BlockPos pos, ItemStack stack) {
    level.addFreshEntity(
        new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack));
  }
}
