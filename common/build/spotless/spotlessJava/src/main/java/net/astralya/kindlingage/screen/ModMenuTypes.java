package net.astralya.kindlingage.screen;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.kindlingage.KindlingAge;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModMenuTypes {
  public static final DeferredRegister<MenuType<?>> MENU_TYPES =
      DeferredRegister.create(KindlingAge.MOD_ID, Registries.MENU);

  public static final RegistrySupplier<MenuType<WickerBasketMenu>> WICKER_BASKET =
      MENU_TYPES.register(
          "wicker_basket", () -> new MenuType<>(WickerBasketMenu::new, FeatureFlags.DEFAULT_FLAGS));

  public static final RegistrySupplier<MenuType<CrudeBagMenu>> CRUDE_BAG =
      MENU_TYPES.register(
          "crude_bag", () -> new MenuType<>(CrudeBagMenu::new, FeatureFlags.DEFAULT_FLAGS));

  private ModMenuTypes() {}

  public static void init() {
    MENU_TYPES.register();
  }
}
