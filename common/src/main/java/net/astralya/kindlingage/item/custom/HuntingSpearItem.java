package net.astralya.kindlingage.item.custom;

import net.astralya.kindlingage.entity.projectile.ThrownSpearEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class HuntingSpearItem extends Item {
  public static final String CONFIG_CATEGORY = "hunting_spear";
  public static final String CONFIG_SMALL_GAME_DAMAGE_MULTIPLIER_KEY =
      "smallGameDamageMultiplier";
  public static final String CONFIG_SMALL_GAME_DAMAGE_BONUS_KEY = "smallGameDamageBonus";
  public static final double DEFAULT_SMALL_GAME_DAMAGE_MULTIPLIER = 1.5D;
  public static final double MIN_SMALL_GAME_DAMAGE_MULTIPLIER = 1.0D;
  public static final double MAX_SMALL_GAME_DAMAGE_MULTIPLIER = 10.0D;
  public static final double DEFAULT_SMALL_GAME_DAMAGE_BONUS = 0.0D;
  public static final double MIN_SMALL_GAME_DAMAGE_BONUS = 0.0D;
  public static final double MAX_SMALL_GAME_DAMAGE_BONUS = 100.0D;
  public static final float PROJECTILE_BASE_DAMAGE = 5.5F;
  public static final float SHOOT_POWER = 2.0F;
  public static final int COOLDOWN_TICKS = 8;

  private static double smallGameDamageMultiplier = DEFAULT_SMALL_GAME_DAMAGE_MULTIPLIER;
  private static double smallGameDamageBonus = DEFAULT_SMALL_GAME_DAMAGE_BONUS;

  public HuntingSpearItem(Properties properties) {
    super(properties);
  }

  public static void applyConfig(double damageMultiplier, double damageBonus) {
    smallGameDamageMultiplier =
        clamp(damageMultiplier, MIN_SMALL_GAME_DAMAGE_MULTIPLIER, MAX_SMALL_GAME_DAMAGE_MULTIPLIER);
    smallGameDamageBonus =
        clamp(damageBonus, MIN_SMALL_GAME_DAMAGE_BONUS, MAX_SMALL_GAME_DAMAGE_BONUS);
  }

  public static double getSmallGameDamageMultiplier() {
    return smallGameDamageMultiplier;
  }

  public static double getSmallGameDamageBonus() {
    return smallGameDamageBonus;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);

    if (stack.isDamageableItem() && stack.getDamageValue() >= stack.getMaxDamage() - 1) {
      return InteractionResultHolder.fail(stack);
    }

    if (!level.isClientSide) {
      ItemStack thrownStack = stack.copy();
      thrownStack.setCount(1);

      if (thrownStack.isDamageableItem()) {
        int newDamage = thrownStack.getDamageValue() + 1;
        if (newDamage < thrownStack.getMaxDamage()) {
          thrownStack.setDamageValue(newDamage);
        }
      }

      ThrownSpearEntity spear = new ThrownSpearEntity(level, player, thrownStack);
      spear.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, SHOOT_POWER, 1.0F);
      spear.setPos(player.getX(), player.getEyeY() - 0.5D, player.getZ());
      spear.pickup =
          player.getAbilities().instabuild
              ? AbstractArrow.Pickup.CREATIVE_ONLY
              : AbstractArrow.Pickup.ALLOWED;

      level.addFreshEntity(spear);
      level.playSound(null, spear, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);

      if (!player.getAbilities().instabuild) {
        player.setItemInHand(hand, ItemStack.EMPTY);
      }

      ItemCooldowns cooldowns = player.getCooldowns();
      cooldowns.addCooldown(this, COOLDOWN_TICKS);
    }

    player.awardStat(Stats.ITEM_USED.get(this));
    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
  }

  @Override
  public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    stack.hurtAndBreak(1, attacker, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
    return true;
  }

  @Override
  public int getEnchantmentValue() {
    return 10;
  }

  private static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }
}
