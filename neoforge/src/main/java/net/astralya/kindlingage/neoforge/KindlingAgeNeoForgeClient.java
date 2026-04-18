package net.astralya.kindlingage.neoforge;

import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.client.renderer.blockentity.ClayPotBlockEntityRenderer;
import net.astralya.kindlingage.client.renderer.blockentity.WickerBasketBlockEntityRenderer;
import net.astralya.kindlingage.client.renderer.entity.ThrownSpearRenderer;
import net.astralya.kindlingage.client.screen.CrudeBagScreen;
import net.astralya.kindlingage.client.screen.WickerBasketScreen;
import net.astralya.kindlingage.entity.ModEntityTypes;
import net.astralya.kindlingage.screen.ModMenuTypes;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class KindlingAgeNeoForgeClient {
    private static final ResourceLocation WICKER_BASKET_LID_ID =
            ResourceLocation.fromNamespaceAndPath(KindlingAge.MOD_ID, "block/wicker_basket_lid");

    private static final ModelResourceLocation WICKER_BASKET_LID_MODEL =
            new ModelResourceLocation(WICKER_BASKET_LID_ID, "standalone");

    private KindlingAgeNeoForgeClient() {
    }

    public static void init(IEventBus modEventBus) {
        KindlingAge.initClient();
        modEventBus.addListener(KindlingAgeNeoForgeClient::registerEntityRenderers);
        modEventBus.addListener(KindlingAgeNeoForgeClient::registerMenuScreens);
        modEventBus.addListener(KindlingAgeNeoForgeClient::registerAdditionalModels);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.THROWN_SPEAR.get(), ThrownSpearRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.CLAY_POT.get(), ClayPotBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.WICKER_BASKET.get(), WickerBasketBlockEntityRenderer::new);
    }

    private static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.WICKER_BASKET.get(), WickerBasketScreen::new);
        event.register(ModMenuTypes.CRUDE_BAG.get(), CrudeBagScreen::new);
    }

    private static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(WICKER_BASKET_LID_MODEL);
    }
}