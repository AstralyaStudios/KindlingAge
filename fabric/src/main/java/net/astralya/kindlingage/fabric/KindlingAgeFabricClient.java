package net.astralya.kindlingage.fabric;

import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.client.renderer.blockentity.ClayPotBlockEntityRenderer;
import net.astralya.kindlingage.client.renderer.entity.ThrownSpearRenderer;
import net.astralya.kindlingage.client.screen.CrudeBagScreen;
import net.astralya.kindlingage.client.screen.WickerBasketScreen;
import net.astralya.kindlingage.entity.ModEntityTypes;
import net.astralya.kindlingage.fabric.client.renderer.blockentity.FabricWickerBasketBlockEntityRenderer;
import net.astralya.kindlingage.screen.ModMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;

public final class KindlingAgeFabricClient implements ClientModInitializer {
    private static final ResourceLocation WICKER_BASKET_LID_ID =
            ResourceLocation.fromNamespaceAndPath(KindlingAge.MOD_ID, "block/wicker_basket_lid");

    @Override
    public void onInitializeClient() {
        KindlingAge.initClient();

        EntityRendererRegistry.register(ModEntityTypes.THROWN_SPEAR.get(), ThrownSpearRenderer::new);

        BlockEntityRenderers.register(ModBlockEntityTypes.CLAY_POT.get(), ClayPotBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.WICKER_BASKET.get(), FabricWickerBasketBlockEntityRenderer::new);

        MenuScreens.register(ModMenuTypes.WICKER_BASKET.get(), WickerBasketScreen::new);
        MenuScreens.register(ModMenuTypes.CRUDE_BAG.get(), CrudeBagScreen::new);

        ModelLoadingPlugin.register(context -> context.addModels(WICKER_BASKET_LID_ID));
    }
}