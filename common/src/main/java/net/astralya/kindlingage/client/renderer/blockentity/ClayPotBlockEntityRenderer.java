package net.astralya.kindlingage.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.astralya.kindlingage.block.entity.custom.ClayPotBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;

public final class ClayPotBlockEntityRenderer implements BlockEntityRenderer<ClayPotBlockEntity> {
  private static final ResourceLocation WATER_SPRITE_ID =
      new ResourceLocation("minecraft", "block/water_still");

  public ClayPotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

  @Override
  public void render(
      ClayPotBlockEntity blockEntity,
      float partialTicks,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay) {
    Level level = blockEntity.getLevel();
    if (level == null) {
      return;
    }

    int water = blockEntity.getWaterMb();
    if (water <= 0) {
      return;
    }

    float y = computeWaterHeight(water);
    int waterColor = BiomeColors.getAverageWaterColor(level, blockEntity.getBlockPos());
    float r = ((waterColor >> 16) & 255) / 255.0F;
    float g = ((waterColor >> 8) & 255) / 255.0F;
    float b = (waterColor & 255) / 255.0F;

    TextureAtlasSprite sprite =
        Minecraft.getInstance()
            .getModelManager()
            .getAtlas(InventoryMenu.BLOCK_ATLAS)
            .getSprite(WATER_SPRITE_ID);

    float inset = 4.01F / 16.0F;
    float maxX = 1.0F - inset;
    float maxZ = 1.0F - inset;
    VertexConsumer consumer = buffer.getBuffer(RenderType.translucent());
    PoseStack.Pose pose = poseStack.last();
    float alpha = 0.85F;

    putVertex(consumer, pose, inset, y, inset, r, g, b, alpha, sprite.getU0(), sprite.getV0(), packedLight, packedOverlay);
    putVertex(consumer, pose, inset, y, maxZ, r, g, b, alpha, sprite.getU0(), sprite.getV1(), packedLight, packedOverlay);
    putVertex(consumer, pose, maxX, y, maxZ, r, g, b, alpha, sprite.getU1(), sprite.getV1(), packedLight, packedOverlay);
    putVertex(consumer, pose, maxX, y, inset, r, g, b, alpha, sprite.getU1(), sprite.getV0(), packedLight, packedOverlay);
  }

  private static void putVertex(
      VertexConsumer consumer,
      PoseStack.Pose pose,
      float x,
      float y,
      float z,
      float r,
      float g,
      float b,
      float a,
      float u,
      float v,
      int packedLight,
      int packedOverlay) {
    consumer
        .vertex(pose.pose(), x, y, z)
        .color(r, g, b, a)
        .uv(u, v)
        .overlayCoords(packedOverlay)
        .uv2(packedLight)
        .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
        .endVertex();
  }

  private static float computeWaterHeight(int waterMb) {
    float minY = 4.02F / 16.0F;
    float maxY = 7.02F / 16.0F;
    int capacity = Math.max(ClayPotBlockEntity.getCapacityMb(), 1);
    float t = Math.max(0.0F, Math.min(waterMb / (float) capacity, 1.0F));
    return minY + (maxY - minY) * t;
  }
}
