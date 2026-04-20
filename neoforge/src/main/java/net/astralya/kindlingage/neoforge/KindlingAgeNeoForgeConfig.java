package net.astralya.kindlingage.neoforge;

import net.astralya.kindlingage.KindlingAgeCommonConfig;
import net.astralya.kindlingage.KindlingAgeConfig;
import net.astralya.kindlingage.block.entity.custom.ClayPotBlockEntity;
import net.astralya.kindlingage.block.entity.custom.FishTrapBlockEntity;
import net.astralya.kindlingage.item.custom.HuntingSpearItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

final class KindlingAgeNeoForgeConfig {
    private static final ModConfigSpec SPEC;

    private static final ModConfigSpec.DoubleValue HUNTING_SPEAR_SMALL_GAME_DAMAGE_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue HUNTING_SPEAR_SMALL_GAME_DAMAGE_BONUS;
    private static final ModConfigSpec.IntValue CLAY_POT_CAPACITY_MB;
    private static final ModConfigSpec.IntValue WET_CLAY_POT_DURATION;
    private static final ModConfigSpec.IntValue FISH_TRAP_CATCH_INTERVAL;
    private static final ModConfigSpec.BooleanValue PREVENT_HAND_BREAKING_LOGS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push(HuntingSpearItem.CONFIG_CATEGORY);

        HUNTING_SPEAR_SMALL_GAME_DAMAGE_MULTIPLIER = builder
                .comment("Multiplier applied to Hunting Spear projectile damage when hitting entities in kindlingage:small_game. Set to 1.0 to disable.")
                .defineInRange(
                        HuntingSpearItem.CONFIG_SMALL_GAME_DAMAGE_MULTIPLIER_KEY,
                        HuntingSpearItem.DEFAULT_SMALL_GAME_DAMAGE_MULTIPLIER,
                        HuntingSpearItem.MIN_SMALL_GAME_DAMAGE_MULTIPLIER,
                        HuntingSpearItem.MAX_SMALL_GAME_DAMAGE_MULTIPLIER
                );

        HUNTING_SPEAR_SMALL_GAME_DAMAGE_BONUS = builder
                .comment("Flat bonus added to Hunting Spear projectile damage when hitting entities in kindlingage:small_game. Set to 0.0 to disable.")
                .defineInRange(
                        HuntingSpearItem.CONFIG_SMALL_GAME_DAMAGE_BONUS_KEY,
                        HuntingSpearItem.DEFAULT_SMALL_GAME_DAMAGE_BONUS,
                        HuntingSpearItem.MIN_SMALL_GAME_DAMAGE_BONUS,
                        HuntingSpearItem.MAX_SMALL_GAME_DAMAGE_BONUS
                );

        builder.pop();
        builder.push(ClayPotBlockEntity.CONFIG_CATEGORY);

        CLAY_POT_CAPACITY_MB = builder
                .comment("Capacity of the Clay Pot in millibuckets (mB). Default 2000 (2 buckets).")
                .defineInRange(
                        ClayPotBlockEntity.CONFIG_CAPACITY_MB_KEY,
                        ClayPotBlockEntity.DEFAULT_CAPACITY_MB,
                        ClayPotBlockEntity.MIN_CAPACITY_MB,
                        ClayPotBlockEntity.MAX_CAPACITY_MB
                );

        WET_CLAY_POT_DURATION = builder
                .comment("Time in ticks required to sun-dry a Wet Clay Pot. Fire or campfire halves this duration.")
                .defineInRange(
                        ClayPotBlockEntity.CONFIG_WET_DURATION_KEY,
                        ClayPotBlockEntity.DEFAULT_WET_DURATION,
                        ClayPotBlockEntity.MIN_WET_DURATION,
                        ClayPotBlockEntity.MAX_WET_DURATION
                );

        FISH_TRAP_CATCH_INTERVAL = builder
                .comment("Time in ticks between fish trap catches while baited and underwater.")
                .defineInRange(
                        FishTrapBlockEntity.CONFIG_CATCH_INTERVAL_KEY,
                        FishTrapBlockEntity.DEFAULT_CATCH_INTERVAL,
                        FishTrapBlockEntity.MIN_CATCH_INTERVAL,
                        FishTrapBlockEntity.MAX_CATCH_INTERVAL
                );

        builder.pop();
        builder.push(KindlingAgeConfig.PROGRESSION_CONFIG_CATEGORY);

        PREVENT_HAND_BREAKING_LOGS = builder
                .comment("If true, players cannot break log blocks with an empty main hand.")
                .define(
                        KindlingAgeConfig.PREVENT_HAND_BREAKING_LOGS_KEY,
                        true
                );

        builder.pop();
        SPEC = builder.build();
    }

    private KindlingAgeNeoForgeConfig() {
    }

    static void init(ModContainer modContainer, IEventBus modEventBus) {
        modContainer.registerConfig(ModConfig.Type.COMMON, SPEC);
        modEventBus.addListener(KindlingAgeNeoForgeConfig::onConfigLoading);
        modEventBus.addListener(KindlingAgeNeoForgeConfig::onConfigReloading);
    }

    private static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            apply();
        }
    }

    private static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            apply();
        }
    }

    private static void apply() {
        KindlingAgeConfig.apply(new KindlingAgeCommonConfig.Values(
                HUNTING_SPEAR_SMALL_GAME_DAMAGE_MULTIPLIER.get(),
                HUNTING_SPEAR_SMALL_GAME_DAMAGE_BONUS.get(),
                CLAY_POT_CAPACITY_MB.get(),
                WET_CLAY_POT_DURATION.get(),
                FISH_TRAP_CATCH_INTERVAL.get(),
                PREVENT_HAND_BREAKING_LOGS.get()
        ));
    }
}
