package net.astralya.kindlingage.fabric.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.block.custom.WickerBasketBlock;
import net.astralya.kindlingage.block.entity.custom.WickerBasketBlockEntity;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class FabricWickerBasketBlockEntityRenderer implements BlockEntityRenderer<WickerBasketBlockEntity> {
    private static final ResourceLocation LID_MODEL_ID =
            ResourceLocation.fromNamespaceAndPath(KindlingAge.MOD_ID, "block/wicker_basket_lid");

    public FabricWickerBasketBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WickerBasketBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof WickerBasketBlock)) {
            return;
        }

        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        float open = blockEntity.getOpenProgress(partialTicks);
        float angle = Mth.lerp(open, 0.0F, 22.5F);

        Direction facing = state.getValue(WickerBasketBlock.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5D, 0.0D, -0.5D);

        poseStack.translate(0.5D, 0.46875D, 0.9375D);
        poseStack.mulPose(Axis.XP.rotationDegrees(angle));
        poseStack.translate(-0.5D, -0.46875D, -0.9375D);

        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher dispatcher = minecraft.getBlockRenderer();
        BakedModel lidModel = ((FabricBakedModelManager) minecraft.getModelManager()).getModel(LID_MODEL_ID);

        RenderType renderType = RenderType.cutout();
        RandomSource random = RandomSource.create();
        long seed = state.getSeed(blockEntity.getBlockPos());

        dispatcher.getModelRenderer().tesselateBlock(
                level,
                lidModel,
                state,
                blockEntity.getBlockPos(),
                poseStack,
                buffer.getBuffer(renderType),
                false,
                random,
                seed,
                packedOverlay
        );

        poseStack.popPose();
    }
}