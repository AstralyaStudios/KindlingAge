package net.astralya.kindlingage.forge;

import dev.architectury.platform.forge.EventBuses;
import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.client.renderer.blockentity.ClayPotBlockEntityRenderer;
import net.astralya.kindlingage.client.renderer.blockentity.DryingRackBlockEntityRenderer;
import net.astralya.kindlingage.client.renderer.blockentity.FishTrapBlockEntityRenderer;
import net.astralya.kindlingage.client.renderer.blockentity.HearthPitBlockEntityRenderer;
import net.astralya.kindlingage.client.renderer.entity.ThrownSpearRenderer;
import net.astralya.kindlingage.client.screen.CrudeBagScreen;
import net.astralya.kindlingage.client.screen.WickerBasketScreen;
import net.astralya.kindlingage.entity.ModEntityTypes;
import net.astralya.kindlingage.screen.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(KindlingAge.MOD_ID)
public final class KindlingAgeForge {
  public KindlingAgeForge() {
    var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    EventBuses.registerModEventBus(KindlingAge.MOD_ID, modEventBus);
    KindlingAgeForgeConfig.init(modEventBus);
    modEventBus.addListener(this::onClientSetup);
    modEventBus.addListener(this::registerRenderers);
    KindlingAge.init();
  }

  private void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(
        () -> {
          MenuScreens.register(ModMenuTypes.WICKER_BASKET.get(), WickerBasketScreen::new);
          MenuScreens.register(ModMenuTypes.CRUDE_BAG.get(), CrudeBagScreen::new);
          ItemBlockRenderTypes.setRenderLayer(ModBlocks.WICKER_BASKET.get(), RenderType.cutout());
          ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEARTH_PIT.get(), RenderType.cutout());
          ItemBlockRenderTypes.setRenderLayer(ModBlocks.REED_MAT.get(), RenderType.cutout());
          EntityRenderers.register(ModEntityTypes.THROWN_SPEAR.get(), ThrownSpearRenderer::new);
        });
  }

  private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(ModBlockEntityTypes.CLAY_POT.get(), ClayPotBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(ModBlockEntityTypes.DRYING_RACK.get(), DryingRackBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(ModBlockEntityTypes.FISH_TRAP.get(), FishTrapBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(ModBlockEntityTypes.HEARTH_PIT.get(), HearthPitBlockEntityRenderer::new);
  }
}
