package net.astralya.kindlingage.fabric;

import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.KindlingAgeCommonConfig;
import net.astralya.kindlingage.KindlingAgeConfig;
import net.astralya.kindlingage.block.entity.custom.ClayPotBlockEntity;
import net.astralya.kindlingage.item.custom.HuntingSpearItem;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

final class KindlingAgeFabricConfig {
    private static final String FILE_NAME = KindlingAge.MOD_ID + "-common.properties";

    private KindlingAgeFabricConfig() {
    }

    static void init() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        KindlingAgeCommonConfig.Values values = read(configPath);
        KindlingAgeConfig.apply(values);
        write(configPath, KindlingAgeConfig.snapshot());
    }

    private static KindlingAgeCommonConfig.Values read(Path configPath) {
        Properties properties = new Properties();
        if (Files.exists(configPath)) {
            try (InputStream inputStream = Files.newInputStream(configPath)) {
                properties.load(inputStream);
            } catch (IOException exception) {
                KindlingAge.LOGGER.error("Failed to read Fabric config from {}", configPath, exception);
            }
        }

        KindlingAgeCommonConfig.Values defaults = KindlingAgeCommonConfig.defaults();
        return new KindlingAgeCommonConfig.Values(
                getDouble(properties, HuntingSpearItem.CONFIG_CATEGORY + "." + HuntingSpearItem.CONFIG_SMALL_GAME_DAMAGE_MULTIPLIER_KEY, defaults.huntingSpearSmallGameDamageMultiplier()),
                getDouble(properties, HuntingSpearItem.CONFIG_CATEGORY + "." + HuntingSpearItem.CONFIG_SMALL_GAME_DAMAGE_BONUS_KEY, defaults.huntingSpearSmallGameDamageBonus()),
                getInt(properties, ClayPotBlockEntity.CONFIG_CATEGORY + "." + ClayPotBlockEntity.CONFIG_CAPACITY_MB_KEY, defaults.clayPotCapacityMb()),
                getInt(properties, ClayPotBlockEntity.CONFIG_CATEGORY + "." + ClayPotBlockEntity.CONFIG_WET_DURATION_KEY, defaults.wetClayPotDuration())
        );
    }

    private static void write(Path configPath, KindlingAgeCommonConfig.Values values) {
        Properties properties = new Properties();
        properties.setProperty(HuntingSpearItem.CONFIG_CATEGORY + "." + HuntingSpearItem.CONFIG_SMALL_GAME_DAMAGE_MULTIPLIER_KEY, Double.toString(values.huntingSpearSmallGameDamageMultiplier()));
        properties.setProperty(HuntingSpearItem.CONFIG_CATEGORY + "." + HuntingSpearItem.CONFIG_SMALL_GAME_DAMAGE_BONUS_KEY, Double.toString(values.huntingSpearSmallGameDamageBonus()));
        properties.setProperty(ClayPotBlockEntity.CONFIG_CATEGORY + "." + ClayPotBlockEntity.CONFIG_CAPACITY_MB_KEY, Integer.toString(values.clayPotCapacityMb()));
        properties.setProperty(ClayPotBlockEntity.CONFIG_CATEGORY + "." + ClayPotBlockEntity.CONFIG_WET_DURATION_KEY, Integer.toString(values.wetClayPotDuration()));

        try {
            Files.createDirectories(configPath.getParent());
            try (OutputStream outputStream = Files.newOutputStream(configPath)) {
                properties.store(outputStream, "Kindling Age common configuration");
            }
        } catch (IOException exception) {
            KindlingAge.LOGGER.error("Failed to write Fabric config to {}", configPath, exception);
        }
    }

    private static double getDouble(Properties properties, String key, double fallback) {
        String raw = properties.getProperty(key);
        if (raw == null) {
            return fallback;
        }

        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException exception) {
            KindlingAge.LOGGER.warn("Invalid double config value for {}: {}", key, raw);
            return fallback;
        }
    }

    private static int getInt(Properties properties, String key, int fallback) {
        String raw = properties.getProperty(key);
        if (raw == null) {
            return fallback;
        }

        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException exception) {
            KindlingAge.LOGGER.warn("Invalid integer config value for {}: {}", key, raw);
            return fallback;
        }
    }
}
