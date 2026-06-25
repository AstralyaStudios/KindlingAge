package net.astralya.kindlingage.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.astralya.kindlingage.block.custom.FishTrapBlock;
import net.astralya.kindlingage.block.entity.custom.FishTrapBlockEntity;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class FishTrapBlockEntityRenderer implements BlockEntityRenderer<FishTrapBlockEntity> {
  private static final int MAX_RENDERED_ITEMS = 8;
  private static final float ITEM_SCALE = 0.5F;
  private static final float STACK_STEP = 0.02F;
  private static final float BASE_Y = 2.01F / 16.0F;
  private static final float FISH_X = 5.0F / 16.0F;
  private static final float SEEDS_X = 11.0F / 16.0F;
  private static final float PILE_Z = 8.0F / 16.0F;
  private static final float[] PILE_ROTATIONS = {0.0F, 27.5F, -18.0F, 41.0F, -33.0F, 14.0F, -9.0F, 36.0F};

  private final ItemRenderer itemRenderer;

  public FishTrapBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    this.itemRenderer = context.getItemRenderer();
  }

  @Override
  public void render(
      FishTrapBlockEntity blockEntity,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay) {
    Direction facing = blockEntity.getBlockState().getValue(FishTrapBlock.FACING);

    renderPile(blockEntity, poseStack, buffer, packedLight, packedOverlay, facing, new ItemStack(ModItems.WILD_SEEDS.get()), blockEntity.getBaitCount(), SEEDS_X, PILE_Z, 17);
    renderPile(blockEntity, poseStack, buffer, packedLight, packedOverlay, facing, blockEntity.getCatchStack(), blockEntity.getCatchCount(), FISH_X, PILE_Z, 53);
  }

  private void renderPile(
      FishTrapBlockEntity blockEntity,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay,
      Direction facing,
      ItemStack stack,
      int count,
      float x,
      float z,
      int rotationOffset) {
    if (stack.isEmpty() || count <= 0) {
      return;
    }

    int renderedCount = Math.min(MAX_RENDERED_ITEMS, count);
    for (int i = 0; i < renderedCount; i++) {
      float rotation = PILE_ROTATIONS[Math.floorMod(rotationOffset + i, PILE_ROTATIONS.length)];

      poseStack.pushPose();
      poseStack.translate(0.5F, 0.0F, 0.5F);
      poseStack.mulPose(Axis.YP.rotationDegrees(rotationForNorthBasis(facing)));
      poseStack.translate(x - 0.5F, BASE_Y + i * STACK_STEP, z - 0.5F);
      poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
      poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
      poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

      itemRenderer.renderStatic(
          stack,
          ItemDisplayContext.GROUND,
          packedLight,
          packedOverlay,
          poseStack,
          buffer,
          blockEntity.getLevel(),
          rotationOffset + i);

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
