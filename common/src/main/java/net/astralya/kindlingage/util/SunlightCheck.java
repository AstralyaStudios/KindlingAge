package net.astralya.kindlingage.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

@SuppressWarnings("unused")
public final class SunlightCheck {
  private final Level level;
  private BlockPos pos;

  private final boolean needsRainCheck;
  private final float peakMultiplier;

  private boolean canSeeSun;

  public SunlightCheck(Level level, BlockPos pos) {
    this.level = level;
    this.pos = pos;

    Biome biome = this.level.getBiome(this.pos).value();
    this.needsRainCheck = biome.hasPrecipitation();

    float tempEffect = 0.3F * (0.8F - biome.getBaseTemperature());
    float humidityEffect = this.needsRainCheck ? -0.15F : 0.0F;
    this.peakMultiplier = 1.0F + tempEffect + humidityEffect;
  }

  public void moveTo(BlockPos newPos) {
    this.pos = newPos;
    recheckCanSeeSun();
  }

  public void recheckCanSeeSun() {
    this.canSeeSun = canSeeSun(this.level, this.pos);
  }

  public boolean canSeeSunNow() {
    return this.canSeeSun;
  }

  public float getPeakMultiplier() {
    return this.peakMultiplier;
  }

  public float getGenerationMultiplier() {
    if (!this.canSeeSun) {
      return 0.0F;
    }

    if (this.needsRainCheck && (this.level.isRaining() || this.level.isThundering())) {
      return this.peakMultiplier * 0.2F;
    }

    return this.peakMultiplier;
  }

  public static float getSunBrightness(Level level, float partialTicks) {
    float time = level.getTimeOfDay(partialTicks);
    float curve = 1.0F - (Mth.cos(time * Mth.TWO_PI) * 2.0F + 0.2F);
    curve = Mth.clamp(curve, 0.0F, 1.0F);
    curve = 1.0F - curve;
    curve = (float) (curve * (1.0D - level.getRainLevel(partialTicks) * 5.0D / 16.0D));
    curve = (float) (curve * (1.0D - level.getThunderLevel(partialTicks) * 5.0D / 16.0D));
    return curve * 0.8F + 0.2F;
  }

  public static boolean canSeeSun(Level level, BlockPos pos) {
    if (level == null) {
      return false;
    }

    if (!level.dimensionType().hasSkyLight()) {
      return false;
    }

    if (level.getSkyDarken() >= 4) {
      return false;
    }

    return level.canSeeSky(pos);
  }
}
