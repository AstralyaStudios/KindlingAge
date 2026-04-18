package net.astralya.kindlingage.entity.projectile;

import net.astralya.kindlingage.entity.ModEntityTypes;
import net.astralya.kindlingage.item.ModItems;
import net.astralya.kindlingage.item.custom.HuntingSpearItem;
import net.astralya.kindlingage.util.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public final class ThrownSpearEntity extends AbstractArrow {
  private static final EntityDataAccessor<Byte> ID_LOYALTY =
      SynchedEntityData.defineId(ThrownSpearEntity.class, EntityDataSerializers.BYTE);

  private boolean dealtDamage;
  private int clientSideReturnSpearTickCount;

  public ThrownSpearEntity(EntityType<? extends ThrownSpearEntity> entityType, Level level) {
    super(entityType, level);
  }

  public ThrownSpearEntity(Level level, LivingEntity shooter, ItemStack pickupItemStack) {
    super(ModEntityTypes.THROWN_SPEAR.get(), shooter, level, pickupItemStack, pickupItemStack);
    this.updateLoyaltyFromItem(pickupItemStack);
  }

  public ThrownSpearEntity(Level level, double x, double y, double z, ItemStack pickupItemStack) {
    super(ModEntityTypes.THROWN_SPEAR.get(), x, y, z, level, pickupItemStack, pickupItemStack);
    this.updateLoyaltyFromItem(pickupItemStack);
  }

  @Override
  protected void defineSynchedData(SynchedEntityData.Builder builder) {
    super.defineSynchedData(builder);
    builder.define(ID_LOYALTY, (byte) 0);
  }

  @Override
  public void tick() {
    if (this.inGroundTime > 2) {
      this.dealtDamage = true;
    }

    Entity owner = this.getOwner();
    int loyalty = this.entityData.get(ID_LOYALTY);

    if (loyalty > 0 && (this.dealtDamage || this.noPhysics)) {
      if (owner == null || !owner.isAlive()) {
        if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
          this.spawnAtLocation(this.getPickupItem(), 0.1F);
        }

        this.discard();
        return;
      }

      this.noPhysics = true;

      Vec3 offset = owner.getEyePosition().subtract(this.position());
      this.setPosRaw(this.getX(), this.getY() + offset.y * 0.008D * loyalty, this.getZ());

      if (this.level().isClientSide) {
        this.yOld = this.getY();
      }

      double acceleration =
          this.level() instanceof ServerLevel serverLevel
              ? EnchantmentHelper.getTridentReturnToOwnerAcceleration(
                  serverLevel, this.getWeaponItem(), this)
              : loyalty;

      this.setDeltaMovement(
          this.getDeltaMovement()
              .scale(0.94D)
              .add(offset.normalize().scale(0.065D * acceleration)));

      if (this.clientSideReturnSpearTickCount++ == 0) {
        this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
      }
    }

    super.tick();
  }

  @Override
  protected ItemStack getDefaultPickupItem() {
    return new ItemStack(ModItems.HUNTING_SPEAR.get());
  }

  @Override
  protected double getDefaultGravity() {
    return 0.05D;
  }

  @Override
  protected void onHitEntity(EntityHitResult result) {
    Entity entity = result.getEntity();
    Entity owner = this.getOwner();
    DamageSource damageSource = this.damageSources().trident(this, owner == null ? this : owner);

    float damage = HuntingSpearItem.PROJECTILE_BASE_DAMAGE;
    if (entity.getType().is(ModTags.EntityTypes.SMALL_GAME)) {
      double multiplier = HuntingSpearItem.getSmallGameDamageMultiplier();
      double bonus = HuntingSpearItem.getSmallGameDamageBonus();
      damage = (float) (damage * multiplier + bonus);
    }

    if (entity.hurt(damageSource, damage)) {
      if (entity instanceof LivingEntity livingTarget) {
        this.doPostHurtEffects(livingTarget);

        if (owner instanceof LivingEntity && this.level() instanceof ServerLevel serverLevel) {
          EnchantmentHelper.doPostAttackEffects(serverLevel, livingTarget, damageSource);
          EnchantmentHelper.modifyKnockback(
              serverLevel, this.getWeaponItem(), livingTarget, damageSource, 0.0F);
        }
      }

      this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

    this.dealtDamage = true;
    this.setDeltaMovement(this.getDeltaMovement().scale(0.01D));
  }

  @Override
  protected boolean tryPickup(Player player) {
    return super.tryPickup(player)
        || this.noPhysics
            && this.ownedBy(player)
            && player.getInventory().add(this.getPickupItem());
  }

  @Override
  public void playerTouch(Player player) {
    if (!this.level().isClientSide && (this.inGround || this.dealtDamage || this.noPhysics)) {
      super.playerTouch(player);
    }
  }

  @Override
  public void tickDespawn() {
    if (this.entityData.get(ID_LOYALTY) <= 0) {
      super.tickDespawn();
    }
  }

  @Override
  protected float getWaterInertia() {
    return 0.99F;
  }

  @Override
  public boolean shouldRender(double x, double y, double z) {
    return true;
  }

  @Override
  public void addAdditionalSaveData(CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    tag.putBoolean("DealtDamage", this.dealtDamage);
  }

  @Override
  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    this.dealtDamage = tag.getBoolean("DealtDamage");
    this.updateLoyaltyFromItem(this.getWeaponItem());
  }

  private void updateLoyaltyFromItem(ItemStack stack) {
    if (this.level() instanceof ServerLevel serverLevel) {
      int loyalty = EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverLevel, stack, this);
      this.entityData.set(ID_LOYALTY, (byte) Math.min(127, loyalty));
    }
  }
}
