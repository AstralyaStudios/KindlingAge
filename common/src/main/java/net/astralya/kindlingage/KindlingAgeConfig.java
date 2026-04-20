package net.astralya.kindlingage;

import net.astralya.kindlingage.block.entity.custom.ClayPotBlockEntity;
import net.astralya.kindlingage.block.entity.custom.FishTrapBlockEntity;
import net.astralya.kindlingage.item.custom.HuntingSpearItem;

public final class KindlingAgeConfig {
  public static final String PROGRESSION_CONFIG_CATEGORY = "progression";
  public static final String PREVENT_HAND_BREAKING_LOGS_KEY = "preventHandBreakingLogs";

  private static boolean preventHandBreakingLogs = true;

  private KindlingAgeConfig() {}

  public static void apply(KindlingAgeCommonConfig.Values values) {
    KindlingAgeCommonConfig.Values sanitized = KindlingAgeCommonConfig.sanitize(values);
    HuntingSpearItem.applyConfig(
        sanitized.huntingSpearSmallGameDamageMultiplier(),
        sanitized.huntingSpearSmallGameDamageBonus());
    ClayPotBlockEntity.applyConfig(sanitized.clayPotCapacityMb(), sanitized.wetClayPotDuration());
    FishTrapBlockEntity.applyConfig(sanitized.fishTrapCatchInterval());
    preventHandBreakingLogs = sanitized.preventHandBreakingLogs();
  }

  public static void reset() {
    apply(KindlingAgeCommonConfig.defaults());
  }

  public static KindlingAgeCommonConfig.Values snapshot() {
    return new KindlingAgeCommonConfig.Values(
        HuntingSpearItem.getSmallGameDamageMultiplier(),
        HuntingSpearItem.getSmallGameDamageBonus(),
        ClayPotBlockEntity.getCapacityMb(),
        ClayPotBlockEntity.getWetClayPotDuration(),
        FishTrapBlockEntity.getCatchInterval(),
        preventHandBreakingLogs);
  }

  public static double huntingSpearSmallGameDamageMultiplier() {
    return HuntingSpearItem.getSmallGameDamageMultiplier();
  }

  public static double huntingSpearSmallGameDamageBonus() {
    return HuntingSpearItem.getSmallGameDamageBonus();
  }

  public static int clayPotCapacityMb() {
    return ClayPotBlockEntity.getCapacityMb();
  }

  public static int wetClayPotDuration() {
    return ClayPotBlockEntity.getWetClayPotDuration();
  }

  public static int fishTrapCatchInterval() {
    return FishTrapBlockEntity.getCatchInterval();
  }

  public static boolean preventHandBreakingLogs() {
    return preventHandBreakingLogs;
  }
}
