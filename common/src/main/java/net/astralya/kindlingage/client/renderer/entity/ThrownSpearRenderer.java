package net.astralya.kindlingage.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.astralya.kindlingage.entity.projectile.ThrownSpearEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class ThrownSpearRenderer extends EntityRenderer<ThrownSpearEntity> {
  private final ItemRenderer itemRenderer;

  public ThrownSpearRenderer(EntityRendererProvider.Context context) {
    super(context);
    itemRenderer = context.getItemRenderer();
  }

  @Override
  public void render(
      ThrownSpearEntity entity,
      float entityYaw,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight) {
    ItemStack stack = entity.getSpearItem();
    if (stack.isEmpty()) {
      return;
    }

    poseStack.pushPose();
    poseStack.translate(0.0D, 0.5D, 0.0D);

    float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F;
    float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());

    poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
    poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));
    poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
    poseStack.scale(1.5F, 1.5F, 1.5F);

    BakedModel model = itemRenderer.getItemModelShaper().getItemModel(stack);
    itemRenderer.render(
        stack, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight, 0, model);

    poseStack.popPose();
    super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
  }

  @Override
  public ResourceLocation getTextureLocation(ThrownSpearEntity entity) {
    return null;
  }
}
