package net.astralya.kindlingage.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.kindlingage.KindlingAge;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeModeTabs {
  public static final DeferredRegister<CreativeModeTab> TABS =
      DeferredRegister.create(KindlingAge.MOD_ID, Registries.CREATIVE_MODE_TAB);

  public static final RegistrySupplier<CreativeModeTab> KINDLING_AGE_TAB =
      TABS.register(
          "main",
          () ->
              CreativeTabRegistry.create(
                  Component.translatable("itemGroup.kindlingage"),
                  () -> new ItemStack(ModItems.FLINT_FLAKE.get())));

  private ModCreativeModeTabs() {}

  public static void init() {
    TABS.register();
  }
}
