package net.astralya.kindlingage;

import net.astralya.kindlingage.block.entity.custom.ClayPotBlockEntity;
import net.astralya.kindlingage.block.entity.custom.FishTrapBlockEntity;
import net.astralya.kindlingage.item.custom.HuntingSpearItem;

public final class KindlingAgeCommonConfig {
  private KindlingAgeCommonConfig() {}

  public static Values defaults() {
    return new Values(
        HuntingSpearItem.DEFAULT_SMALL_GAME_DAMAGE_MULTIPLIER,
        HuntingSpearItem.DEFAULT_SMALL_GAME_DAMAGE_BONUS,
        ClayPotBlockEntity.DEFAULT_CAPACITY_MB,
        ClayPotBlockEntity.DEFAULT_WET_DURATION,
        FishTrapBlockEntity.DEFAULT_CATCH_INTERVAL,
        true);
  }

  public static Values sanitize(Values values) {
    return new Values(
        clamp(
            values.huntingSpearSmallGameDamageMultiplier(),
            HuntingSpearItem.MIN_SMALL_GAME_DAMAGE_MULTIPLIER,
            HuntingSpearItem.MAX_SMALL_GAME_DAMAGE_MULTIPLIER),
        clamp(
            values.huntingSpearSmallGameDamageBonus(),
            HuntingSpearItem.MIN_SMALL_GAME_DAMAGE_BONUS,
            HuntingSpearItem.MAX_SMALL_GAME_DAMAGE_BONUS),
        clamp(
            values.clayPotCapacityMb(),
            ClayPotBlockEntity.MIN_CAPACITY_MB,
            ClayPotBlockEntity.MAX_CAPACITY_MB),
        clamp(
            values.wetClayPotDuration(),
            ClayPotBlockEntity.MIN_WET_DURATION,
            ClayPotBlockEntity.MAX_WET_DURATION),
        clamp(
            values.fishTrapCatchInterval(),
            FishTrapBlockEntity.MIN_CATCH_INTERVAL,
            FishTrapBlockEntity.MAX_CATCH_INTERVAL),
        values.preventHandBreakingLogs());
  }

  private static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  private static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }

  public record Values(
      double huntingSpearSmallGameDamageMultiplier,
      double huntingSpearSmallGameDamageBonus,
      int clayPotCapacityMb,
      int wetClayPotDuration,
      int fishTrapCatchInterval,
      boolean preventHandBreakingLogs) {}
}
