package net.astralya.kindlingage.fabric;

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
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public final class KindlingAgeFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KindlingAge.initClient();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WICKER_BASKET.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HEARTH_PIT.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REED_MAT.get(), RenderType.cutout());

        EntityRendererRegistry.register(ModEntityTypes.THROWN_SPEAR.get(), ThrownSpearRenderer::new);

        BlockEntityRenderers.register(ModBlockEntityTypes.CLAY_POT.get(), ClayPotBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.DRYING_RACK.get(), DryingRackBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.FISH_TRAP.get(), FishTrapBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.HEARTH_PIT.get(), HearthPitBlockEntityRenderer::new);

        MenuScreens.register(ModMenuTypes.WICKER_BASKET.get(), WickerBasketScreen::new);
        MenuScreens.register(ModMenuTypes.CRUDE_BAG.get(), CrudeBagScreen::new);
    }
}
