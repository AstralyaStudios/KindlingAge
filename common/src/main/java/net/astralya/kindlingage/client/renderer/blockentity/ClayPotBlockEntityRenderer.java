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
      ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");

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

    float u0 = sprite.getU0();
    float u1 = sprite.getU1();
    float v0 = sprite.getV0();
    float v1 = sprite.getV1();

    float inset = 4.01F / 16.0F;
    float maxX = 1.0F - inset;
    float maxZ = 1.0F - inset;

    VertexConsumer consumer = buffer.getBuffer(RenderType.translucent());
    PoseStack.Pose pose = poseStack.last();

    float alpha = 0.85F;

    putVertex(
        consumer,
        pose,
        inset,
        y,
        inset,
        r,
        g,
        b,
        alpha,
        u0,
        v0,
        packedLight,
        packedOverlay,
        0.0F,
        1.0F,
        0.0F);
    putVertex(
        consumer,
        pose,
        inset,
        y,
        maxZ,
        r,
        g,
        b,
        alpha,
        u0,
        v1,
        packedLight,
        packedOverlay,
        0.0F,
        1.0F,
        0.0F);
    putVertex(
        consumer,
        pose,
        maxX,
        y,
        maxZ,
        r,
        g,
        b,
        alpha,
        u1,
        v1,
        packedLight,
        packedOverlay,
        0.0F,
        1.0F,
        0.0F);
    putVertex(
        consumer,
        pose,
        maxX,
        y,
        inset,
        r,
        g,
        b,
        alpha,
        u1,
        v0,
        packedLight,
        packedOverlay,
        0.0F,
        1.0F,
        0.0F);
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
      int packedOverlay,
      float nx,
      float ny,
      float nz) {
    consumer.addVertex(pose.pose(), x, y, z);
    consumer.setColor(r, g, b, a);
    consumer.setUv(u, v);
    consumer.setOverlay(packedOverlay);
    consumer.setLight(packedLight);
    consumer.setNormal(pose, nx, ny, nz);
  }

  private static float computeWaterHeight(int waterMb) {
    float minY = 4.02F / 16.0F;
    float maxY = 7.02F / 16.0F;

    int capacity = ClayPotBlockEntity.getCapacityMb();
    if (capacity <= 0) {
      capacity = 1;
    }

    float t = waterMb / (float) capacity;
    if (t < 0.0F) {
      t = 0.0F;
    }
    if (t > 1.0F) {
      t = 1.0F;
    }

    return minY + (maxY - minY) * t;
  }
}
