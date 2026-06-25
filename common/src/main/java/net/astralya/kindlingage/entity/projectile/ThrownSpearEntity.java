package net.astralya.kindlingage.entity.projectile;

import net.astralya.kindlingage.entity.ModEntityTypes;
import net.astralya.kindlingage.item.ModItems;
import net.astralya.kindlingage.item.custom.HuntingSpearItem;
import net.astralya.kindlingage.util.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public final class ThrownSpearEntity extends AbstractArrow {
  private static final String PICKUP_ITEM_TAG = "SpearItem";

  private ItemStack spearItem = new ItemStack(ModItems.HUNTING_SPEAR.get());
  private boolean dealtDamage;

  public ThrownSpearEntity(EntityType<? extends ThrownSpearEntity> entityType, Level level) {
    super(entityType, level);
  }

  public ThrownSpearEntity(Level level, LivingEntity shooter, ItemStack pickupItemStack) {
    super(ModEntityTypes.THROWN_SPEAR.get(), shooter, level);
    this.spearItem = pickupItemStack.copy();
  }

  @Override
  public void tick() {
    if (this.inGroundTime > 2) {
      this.dealtDamage = true;
    }

    super.tick();
  }

  @Override
  protected ItemStack getPickupItem() {
    return this.spearItem.copy();
  }

  public ItemStack getSpearItem() {
    return this.spearItem;
  }

  @Override
  protected void onHitEntity(EntityHitResult result) {
    Entity entity = result.getEntity();
    Entity owner = this.getOwner();
    DamageSource damageSource = this.damageSources().trident(this, owner == null ? this : owner);

    float damage = HuntingSpearItem.PROJECTILE_BASE_DAMAGE;
    if (entity.getType().is(ModTags.EntityTypes.SMALL_GAME)) {
      damage =
          (float)
              (damage * HuntingSpearItem.getSmallGameDamageMultiplier()
                  + HuntingSpearItem.getSmallGameDamageBonus());
    }

    if (entity.hurt(damageSource, damage)) {
      if (entity instanceof LivingEntity livingTarget) {
        this.doPostHurtEffects(livingTarget);
      }

      this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

    this.dealtDamage = true;
    this.setDeltaMovement(this.getDeltaMovement().scale(0.01D));
  }

  @Override
  protected boolean tryPickup(Player player) {
    return super.tryPickup(player) || this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
  }

  @Override
  public void playerTouch(Player player) {
    if (!this.level().isClientSide && (this.inGround || this.dealtDamage)) {
      super.playerTouch(player);
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
    if (!this.spearItem.isEmpty()) {
      tag.put(PICKUP_ITEM_TAG, this.spearItem.save(new CompoundTag()));
    }
  }

  @Override
  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    this.dealtDamage = tag.getBoolean("DealtDamage");
    if (tag.contains(PICKUP_ITEM_TAG, 10)) {
      this.spearItem = ItemStack.of(tag.getCompound(PICKUP_ITEM_TAG));
    }
  }
}
