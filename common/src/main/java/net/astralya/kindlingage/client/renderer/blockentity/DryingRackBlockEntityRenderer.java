package net.astralya.kindlingage.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.astralya.kindlingage.block.custom.DryingRackBlock;
import net.astralya.kindlingage.block.entity.custom.DryingRackBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class DryingRackBlockEntityRenderer implements BlockEntityRenderer<DryingRackBlockEntity> {
  private static final float[] SLOT_X = {4.25F / 16.0F, 8.0F / 16.0F, 11.5F / 16.0F};
  private static final float[] SLOT_Y = {5.5F / 16.0F, 11.5F / 16.0F};
  private static final float SLOT_Z = 8.0F / 16.0F;
  private static final float ITEM_SCALE = 0.5F;

  private final ItemRenderer itemRenderer;

  public DryingRackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    this.itemRenderer = context.getItemRenderer();
  }

  @Override
  public void render(
      DryingRackBlockEntity blockEntity,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay) {
    Direction facing = blockEntity.getBlockState().getValue(DryingRackBlock.FACING);

    for (int slot = 0; slot < DryingRackBlockEntity.SLOT_COUNT; slot++) {
      ItemStack stack = blockEntity.getRenderItem(slot);
      if (stack.isEmpty()) {
        continue;
      }

      int row = slot / 3;
      int column = slot % 3;

      poseStack.pushPose();
      poseStack.translate(0.5F, 0.0F, 0.5F);
      poseStack.mulPose(Axis.YP.rotationDegrees(rotationForNorthBasis(facing)));
      poseStack.translate(SLOT_X[column] - 0.5F, SLOT_Y[row], SLOT_Z - 0.5F);
      poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
      poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

      itemRenderer.renderStatic(
          stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer, blockEntity.getLevel(), slot);

      poseStack.popPose();
    }
  }

  private static float rotationForNorthBasis(Direction facing) {
    return switch (facing) {
      case NORTH -> 0.0F;
      case SOUTH -> 180.0F;
      case EAST -> 90.0F;
      case WEST -> -90.0F;
      default -> 0.0F;
    };
  }
}
