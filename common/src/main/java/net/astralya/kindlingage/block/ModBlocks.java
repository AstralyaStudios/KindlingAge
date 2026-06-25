package net.astralya.kindlingage.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.block.custom.ClayPotBlock;
import net.astralya.kindlingage.block.custom.DryingRackBlock;
import net.astralya.kindlingage.block.custom.FishTrapBlock;
import net.astralya.kindlingage.block.custom.HearthPitBlock;
import net.astralya.kindlingage.block.custom.ReedMatBlock;
import net.astralya.kindlingage.block.custom.WetClayPotBlock;
import net.astralya.kindlingage.block.custom.WickerBasketBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
  public static final DeferredRegister<Block> BLOCKS =
      DeferredRegister.create(KindlingAge.MOD_ID, Registries.BLOCK);

  public static final RegistrySupplier<Block> WICKER_BASKET =
      BLOCKS.register(
          "wicker_basket",
          () ->
              new WickerBasketBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.WOOD)
                      .strength(0.6F)
                      .sound(SoundType.GRASS)
                      .noOcclusion()));

  public static final RegistrySupplier<Block> REED_MAT =
      BLOCKS.register(
          "reed_mat",
          () ->
              new ReedMatBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.SAND)
                      .strength(0.2F)
                      .sound(SoundType.GRASS)
                      .noOcclusion()));

  public static final RegistrySupplier<Block> DRYING_RACK =
      BLOCKS.register(
          "drying_rack",
          () ->
              new DryingRackBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.WOOD)
                      .strength(0.8F)
                      .sound(SoundType.WOOD)
                      .noOcclusion()));

  public static final RegistrySupplier<Block> CLAY_POT =
      BLOCKS.register(
          "clay_pot",
          () ->
              new ClayPotBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.TERRACOTTA_ORANGE)
                      .strength(1.0F)
                      .sound(SoundType.DECORATED_POT)
                      .noOcclusion()));

  public static final RegistrySupplier<Block> WET_CLAY_POT =
      BLOCKS.register(
          "wet_clay_pot",
          () ->
              new WetClayPotBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.CLAY)
                      .strength(0.5F)
                      .sound(SoundType.GRAVEL)
                      .noOcclusion()));

  public static final RegistrySupplier<Block> HEARTH_PIT =
      BLOCKS.register(
          "hearth_pit",
          () ->
              new HearthPitBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.STONE)
                      .strength(1.5F)
                      .sound(SoundType.STONE)
                      .noOcclusion()));

  public static final RegistrySupplier<Block> FISH_TRAP =
      BLOCKS.register(
          "fish_trap",
          () ->
              new FishTrapBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.WOOD)
                      .strength(0.8F)
                      .sound(SoundType.WOOD)
                      .noOcclusion()));

  private ModBlocks() {}

  public static void init() {
    BLOCKS.register();
  }
}
