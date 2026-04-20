package net.astralya.kindlingage.neoforge;

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
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public final class KindlingAgeNeoForgeClient {
    private KindlingAgeNeoForgeClient() {
    }

    public static void init(IEventBus modEventBus) {
        KindlingAge.initClient();
        modEventBus.addListener(KindlingAgeNeoForgeClient::onClientSetup);
        modEventBus.addListener(KindlingAgeNeoForgeClient::registerEntityRenderers);
        modEventBus.addListener(KindlingAgeNeoForgeClient::registerMenuScreens);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(
                () -> {
                    ItemBlockRenderTypes.setRenderLayer(ModBlocks.WICKER_BASKET.get(), RenderType.cutout());
                    ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEARTH_PIT.get(), RenderType.cutout());
                    ItemBlockRenderTypes.setRenderLayer(ModBlocks.REED_MAT.get(), RenderType.cutout());
                });
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.THROWN_SPEAR.get(), ThrownSpearRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.CLAY_POT.get(), ClayPotBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.DRYING_RACK.get(), DryingRackBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.FISH_TRAP.get(), FishTrapBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.HEARTH_PIT.get(), HearthPitBlockEntityRenderer::new);
    }

    private static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.WICKER_BASKET.get(), WickerBasketScreen::new);
        event.register(ModMenuTypes.CRUDE_BAG.get(), CrudeBagScreen::new);
    }
}
