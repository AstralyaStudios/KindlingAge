package net.astralya.kindlingage;

import net.astralya.kindlingage.block.entity.custom.ClayPotBlockEntity;
import net.astralya.kindlingage.block.entity.custom.FishTrapBlockEntity;
import net.astralya.kindlingage.item.custom.HuntingSpearItem;

public final class KindlingAgeConfig {
  public static final String PROGRESSION_CONFIG_CATEGORY = "progression";
  public static final String PREVENT_HAND_BREAKING_LOGS_KEY = "preventHandBreakingLogs";
  public static final String REED_MAT_ALLOWS_SLEEPING_KEY = "reedMatAllowsSleeping";

  private static boolean preventHandBreakingLogs = false;
  private static boolean reedMatAllowsSleeping = false;

  private KindlingAgeConfig() {}

  public static void apply(KindlingAgeCommonConfig.Values values) {
    KindlingAgeCommonConfig.Values sanitized = KindlingAgeCommonConfig.sanitize(values);
    HuntingSpearItem.applyConfig(
        sanitized.huntingSpearSmallGameDamageMultiplier(),
        sanitized.huntingSpearSmallGameDamageBonus());
    ClayPotBlockEntity.applyConfig(sanitized.clayPotCapacityMb(), sanitized.wetClayPotDuration());
    FishTrapBlockEntity.applyConfig(sanitized.fishTrapCatchInterval());
    preventHandBreakingLogs = sanitized.preventHandBreakingLogs();
    reedMatAllowsSleeping = sanitized.reedMatAllowsSleeping();
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
        preventHandBreakingLogs,
        reedMatAllowsSleeping);
  }

  public static boolean preventHandBreakingLogs() {
    return preventHandBreakingLogs;
  }

  public static boolean reedMatAllowsSleeping() {
    return reedMatAllowsSleeping;
  }
}
