package net.astralya.kindlingage.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.entity.projectile.ThrownSpearEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntityTypes {
  public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
      DeferredRegister.create(
          KindlingAge.MOD_ID, net.minecraft.core.registries.Registries.ENTITY_TYPE);

  public static final RegistrySupplier<EntityType<ThrownSpearEntity>> THROWN_SPEAR =
      ENTITY_TYPES.register(
          "thrown_spear",
          () ->
              EntityType.Builder.<ThrownSpearEntity>of(ThrownSpearEntity::new, MobCategory.MISC)
                  .sized(0.5F, 0.5F)
                  .clientTrackingRange(4)
                  .updateInterval(10)
                  .build("thrown_spear"));

  private ModEntityTypes() {}

  public static void init() {
    ENTITY_TYPES.register();
  }
}
