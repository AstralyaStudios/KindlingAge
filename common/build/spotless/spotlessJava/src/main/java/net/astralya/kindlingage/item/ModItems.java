package net.astralya.kindlingage.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.item.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public final class ModItems {
  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(KindlingAge.MOD_ID, Registries.ITEM);

  public static final RegistrySupplier<Item> FLINT_FLAKE =
      ITEMS.register("flint_flake", () -> new Item(defaultProperties()));

  public static final RegistrySupplier<Item> PLANT_FIBER =
      ITEMS.register("plant_fiber", () -> new Item(defaultProperties()));

  public static final RegistrySupplier<Item> FIBER_CORD =
      ITEMS.register("fiber_cord", () -> new Item(defaultProperties()));

  public static final RegistrySupplier<Item> FLINT_KNIFE =
      ITEMS.register(
          "flint_knife",
          () ->
              new FlintKnifeItem(
                  ModToolTiers.FLINT, ModItems.PLANT_FIBER, 1, defaultProperties().durability(32)));

  public static final RegistrySupplier<Item> FLINT_HATCHET =
      ITEMS.register(
          "flint_hatchet",
          () -> new FlintHatchetItem(ModToolTiers.FLINT, defaultProperties().durability(64)));

  public static final RegistrySupplier<Item> HUNTING_SPEAR =
      ITEMS.register(
          "hunting_spear",
          () ->
              new HuntingSpearItem(
                  weaponProperties().attributes(HuntingSpearItem.createAttributes())));

  public static final RegistrySupplier<Item> REED_SCOOP =
      ITEMS.register("reed_scoop", () -> new ReedScoopItem(defaultProperties().stacksTo(1), false));

  public static final RegistrySupplier<Item> WATER_REED_SCOOP =
      ITEMS.register(
          "water_reed_scoop", () -> new ReedScoopItem(defaultProperties().stacksTo(1), true));

  public static final RegistrySupplier<Item> CRUDE_BAG =
      ITEMS.register("crude_bag", () -> new CrudeBagItem(defaultProperties().stacksTo(1)));

  public static final RegistrySupplier<Item> REED_MAT =
      ITEMS.register(
          "reed_mat", () -> new BlockItem(ModBlocks.REED_MAT.get(), defaultProperties()));

  public static final RegistrySupplier<Item> WET_CLAY_POT =
      ITEMS.register(
          "wet_clay_pot", () -> new BlockItem(ModBlocks.WET_CLAY_POT.get(), defaultProperties()));

  public static final RegistrySupplier<Item> CLAY_POT =
      ITEMS.register(
          "clay_pot", () -> new BlockItem(ModBlocks.CLAY_POT.get(), defaultProperties()));

  public static final RegistrySupplier<Item> WICKER_BASKET =
      ITEMS.register(
          "wicker_basket", () -> new BlockItem(ModBlocks.WICKER_BASKET.get(), defaultProperties()));

  private ModItems() {}

  private static Item.Properties defaultProperties() {
    return new Item.Properties().arch$tab(ModCreativeModeTabs.KINDLING_AGE_TAB);
  }

  private static Item.Properties weaponProperties() {
    return defaultProperties().durability(96).stacksTo(1);
  }

  public static void init() {
    ITEMS.register();
  }
}
