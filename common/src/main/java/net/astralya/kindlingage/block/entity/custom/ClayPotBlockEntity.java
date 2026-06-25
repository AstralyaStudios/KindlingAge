package net.astralya.kindlingage.block.entity.custom;

import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ClayPotBlockEntity extends BlockEntity {
  public static final String CONFIG_CATEGORY = "clay_pot";
  public static final String CONFIG_CAPACITY_MB_KEY = "capacityMb";
  public static final String CONFIG_WET_DURATION_KEY = "wetClayPotDuration";
  public static final int DEFAULT_CAPACITY_MB = 2000;
  public static final int MIN_CAPACITY_MB = 250;
  public static final int MAX_CAPACITY_MB = 16000;
  public static final int DEFAULT_WET_DURATION = 2400;
  public static final int MIN_WET_DURATION = 200;
  public static final int MAX_WET_DURATION = 72000;

  private static int capacityMb = DEFAULT_CAPACITY_MB;
  private static int wetClayPotDuration = DEFAULT_WET_DURATION;

  private float dryingProgress;
  private int waterMb;

  public ClayPotBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.CLAY_POT.get(), pos, state);
  }

  public static void applyConfig(int capacity) {
    capacityMb = clamp(capacity, MIN_CAPACITY_MB, MAX_CAPACITY_MB);
  }

  public static void applyConfig(int capacity, int dryingDuration) {
    capacityMb = clamp(capacity, MIN_CAPACITY_MB, MAX_CAPACITY_MB);
    wetClayPotDuration = clamp(dryingDuration, MIN_WET_DURATION, MAX_WET_DURATION);
  }

  public static int getCapacityMb() {
    return capacityMb;
  }

  public static int getWetClayPotDuration() {
    return wetClayPotDuration;
  }

  public int getWaterMb() {
    return waterMb;
  }

  public int fillWater(int amountMb) {
    if (amountMb <= 0) {
      return 0;
    }

    int accepted = Math.min(getCapacityMb() - waterMb, amountMb);
    if (accepted > 0) {
      waterMb += accepted;
      sync();
    }

    return accepted;
  }

  public int drainWater(int amountMb) {
    if (amountMb <= 0) {
      return 0;
    }

    int removed = Math.min(waterMb, amountMb);
    if (removed > 0) {
      waterMb -= removed;
      sync();
    }

    return removed;
  }

  public boolean tickDryingFinished(Level level, BlockPos pos, boolean heated) {
    int target = getWetClayPotDuration();
    if (target <= 0) {
      return true;
    }

    float increment = heated ? 2.0F : getSunIncrement(level, pos);
    if (increment <= 0.0F) {
      return false;
    }

    dryingProgress += increment;
    setChanged();

    return dryingProgress >= target;
  }

  private static float getSunIncrement(Level level, BlockPos pos) {
    if (level == null || level.isClientSide) {
      return 0.0F;
    }

    return level.isDay() && level.canSeeSky(pos.above()) ? 1.0F : 0.0F;
  }

  private void sync() {
    setChanged();

    Level level = getLevel();
    if (level != null && !level.isClientSide) {
      BlockState state = getBlockState();
      level.sendBlockUpdated(worldPosition, state, state, 3);
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    tag.putInt("WaterMb", waterMb);
    tag.putFloat("DryingProgress", dryingProgress);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    waterMb = clamp(tag.getInt("WaterMb"), 0, getCapacityMb());
    dryingProgress = Math.max(0.0F, Math.min(tag.getFloat("DryingProgress"), getWetClayPotDuration()));
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag tag = new CompoundTag();
    saveAdditional(tag);
    return tag;
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  private static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }
}
