package net.astralya.kindlingage.block.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.block.entity.custom.ClayPotBlockEntity;
import net.astralya.kindlingage.block.entity.custom.DryingRackBlockEntity;
import net.astralya.kindlingage.block.entity.custom.FishTrapBlockEntity;
import net.astralya.kindlingage.block.entity.custom.HearthPitBlockEntity;
import net.astralya.kindlingage.block.entity.custom.WickerBasketBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntityTypes {
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
      DeferredRegister.create(KindlingAge.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

  public static final RegistrySupplier<BlockEntityType<ClayPotBlockEntity>> CLAY_POT =
      BLOCK_ENTITY_TYPES.register(
          "clay_pot",
          () ->
              BlockEntityType.Builder.of(
                      ClayPotBlockEntity::new,
                      ModBlocks.WET_CLAY_POT.get(),
                      ModBlocks.CLAY_POT.get())
                  .build(null));

  public static final RegistrySupplier<BlockEntityType<WickerBasketBlockEntity>> WICKER_BASKET =
      BLOCK_ENTITY_TYPES.register(
          "wicker_basket",
          () ->
              BlockEntityType.Builder.of(
                      WickerBasketBlockEntity::new, ModBlocks.WICKER_BASKET.get())
                  .build(null));

  public static final RegistrySupplier<BlockEntityType<HearthPitBlockEntity>> HEARTH_PIT =
      BLOCK_ENTITY_TYPES.register(
          "hearth_pit",
          () ->
              BlockEntityType.Builder.of(HearthPitBlockEntity::new, ModBlocks.HEARTH_PIT.get())
                  .build(null));

  public static final RegistrySupplier<BlockEntityType<FishTrapBlockEntity>> FISH_TRAP =
      BLOCK_ENTITY_TYPES.register(
          "fish_trap",
          () ->
              BlockEntityType.Builder.of(FishTrapBlockEntity::new, ModBlocks.FISH_TRAP.get())
                  .build(null));

  public static final RegistrySupplier<BlockEntityType<DryingRackBlockEntity>> DRYING_RACK =
      BLOCK_ENTITY_TYPES.register(
          "drying_rack",
          () ->
              BlockEntityType.Builder.of(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK.get())
                  .build(null));

  private ModBlockEntityTypes() {}

  public static void init() {
    BLOCK_ENTITY_TYPES.register();
  }
}
