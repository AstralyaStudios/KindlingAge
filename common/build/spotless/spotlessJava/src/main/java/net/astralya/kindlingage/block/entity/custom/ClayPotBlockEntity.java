package net.astralya.kindlingage.block.entity.custom;

import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.util.SunlightCheck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ClayPotBlockEntity extends BlockEntity {
  public static final String CONFIG_CATEGORY = "blocks";
  public static final String CONFIG_CAPACITY_MB_KEY = "clayPotCapacityMb";
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

  private SunlightCheck sunlightCheck;

  public ClayPotBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.CLAY_POT.get(), pos, state);
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

    int capacity = getCapacityMb();
    int space = capacity - waterMb;
    int accepted = Math.min(space, amountMb);

    if (accepted > 0) {
      waterMb += accepted;
      if (waterMb > capacity) {
        waterMb = capacity;
      }
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
      if (waterMb < 0) {
        waterMb = 0;
      }
      sync();
    }

    return removed;
  }

  public void resetDrying() {
    if (dryingProgress != 0.0F) {
      dryingProgress = 0.0F;
      setChanged();
    }
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

  private float getSunIncrement(Level level, BlockPos pos) {
    if (level == null) {
      return 0.0F;
    }

    if (sunlightCheck == null) {
      sunlightCheck = new SunlightCheck(level, pos);
    } else {
      sunlightCheck.moveTo(pos);
    }

    sunlightCheck.recheckCanSeeSun();
    return sunlightCheck.getGenerationMultiplier();
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
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putInt("WaterMb", waterMb);
    tag.putFloat("DryingProgress", dryingProgress);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);

    int capacity = getCapacityMb();

    waterMb = tag.getInt("WaterMb");
    if (waterMb < 0) {
      waterMb = 0;
    } else if (waterMb > capacity) {
      waterMb = capacity;
    }

    dryingProgress = tag.getFloat("DryingProgress");
    if (dryingProgress < 0.0F) {
      dryingProgress = 0.0F;
    } else if (dryingProgress > getWetClayPotDuration()) {
      dryingProgress = getWetClayPotDuration();
    }

    sunlightCheck = null;
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = new CompoundTag();
    saveAdditional(tag, registries);
    return tag;
  }

  public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
    loadAdditional(tag, registries);
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  private static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }
}
