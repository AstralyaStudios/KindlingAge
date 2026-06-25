package net.astralya.kindlingage.client.screen;

import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.screen.CrudeBagMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class CrudeBagScreen extends AbstractContainerScreen<CrudeBagMenu> {
  private static final ResourceLocation TEXTURE =
      new ResourceLocation(KindlingAge.MOD_ID, "textures/gui/container/crude_bag.png");

  public CrudeBagScreen(CrudeBagMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    imageWidth = 176;
    imageHeight = 150;
    inventoryLabelY = 40;
  }

  @Override
  protected void init() {
    super.init();
    titleLabelX = (imageWidth - font.width(title)) / 2;
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    renderBackground(graphics);
    super.render(graphics, mouseX, mouseY, partialTick);
    renderTooltip(graphics, mouseX, mouseY);
  }
}
