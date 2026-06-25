package net.astralya.kindlingage.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.astralya.kindlingage.block.entity.custom.HearthPitBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class HearthPitBlockEntityRenderer implements BlockEntityRenderer<HearthPitBlockEntity> {
  private static final float ITEM_SCALE = 0.5F;
  private static final float ITEM_1_X = 4.0F / 16.0F;
  private static final float ITEM_1_Y = 1.0F / 16.0F;
  private static final float ITEM_1_Z = 9.0F / 16.0F;
  private static final float ITEM_2_X = 11.0F / 16.0F;
  private static final float ITEM_2_Y = 1.0F / 16.0F;
  private static final float ITEM_2_Z = 8.0F / 16.0F;
  private static final float ITEM_1_TILT = -17.5F;
  private static final float ITEM_2_TILT = 10.0F;

  private final ItemRenderer itemRenderer;

  public HearthPitBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    this.itemRenderer = context.getItemRenderer();
  }

  @Override
  public void render(
      HearthPitBlockEntity blockEntity,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay) {
    ItemStack first = blockEntity.getRenderItem(0);
    if (!first.isEmpty()) {
      renderItem(
          blockEntity,
          poseStack,
          buffer,
          packedLight,
          packedOverlay,
          first,
          ITEM_1_X,
          ITEM_1_Y,
          ITEM_1_Z,
          ITEM_1_TILT,
          (int) blockEntity.getBlockPos().asLong());
    }

    ItemStack second = blockEntity.getRenderItem(1);
    if (!second.isEmpty()) {
      renderItem(
          blockEntity,
          poseStack,
          buffer,
          packedLight,
          packedOverlay,
          second,
          ITEM_2_X,
          ITEM_2_Y,
          ITEM_2_Z,
          ITEM_2_TILT,
          (int) blockEntity.getBlockPos().asLong() + 1);
    }
  }

  private void renderItem(
      HearthPitBlockEntity blockEntity,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay,
      ItemStack stack,
      float x,
      float y,
      float z,
      float zTilt,
      int seed) {
    poseStack.pushPose();
    poseStack.translate(0.5F, 0.0F, 0.5F);
    poseStack.translate(x - 0.5F, y, z - 0.5F);
    poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
    poseStack.mulPose(Axis.ZP.rotationDegrees(zTilt));
    poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

    itemRenderer.renderStatic(
        stack,
        ItemDisplayContext.FIXED,
        packedLight,
        packedOverlay,
        poseStack,
        buffer,
        blockEntity.getLevel(),
        seed);

    poseStack.popPose();
  }
}
