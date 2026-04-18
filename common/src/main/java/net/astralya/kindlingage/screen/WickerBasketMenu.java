package net.astralya.kindlingage.screen;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class WickerBasketMenu extends AbstractContainerMenu {
  public static final int COLUMNS = 9;
  public static final int ROWS = 2;
  public static final int CONTAINER_SLOTS = COLUMNS * ROWS;

  private final Container container;

  public WickerBasketMenu(int syncId, Inventory playerInventory) {
    this(syncId, playerInventory, new SimpleContainer(CONTAINER_SLOTS));
  }

  public WickerBasketMenu(int syncId, Inventory playerInventory, Container container) {
    super(ModMenuTypes.WICKER_BASKET.get(), syncId);
    this.container = container;

    checkContainerSize(container, CONTAINER_SLOTS);
    container.startOpen(playerInventory.player);

    int slotIndex = 0;
    int containerX = 8;
    int containerY = 18;

    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLUMNS; col++) {
        this.addSlot(
            new Slot(container, slotIndex++, containerX + col * 18, containerY + row * 18));
      }
    }

    int playerInvX = 8;
    int playerInvY = 18 + (ROWS * 18) + 30;

    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        int index = col + row * 9 + 9;
        this.addSlot(
            new Slot(playerInventory, index, playerInvX + col * 18, playerInvY + row * 18));
      }
    }

    int hotbarY = playerInvY + 58;

    for (int col = 0; col < 9; col++) {
      this.addSlot(new Slot(playerInventory, col, playerInvX + col * 18, hotbarY));
    }

    this.addDataSlots(new SimpleContainerData(0));
  }

  public Container getContainer() {
    return this.container;
  }

  @Override
  public boolean stillValid(Player player) {
    return this.container.stillValid(player);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    Slot slot = this.slots.get(index);
    if (!slot.hasItem()) {
      return ItemStack.EMPTY;
    }

    ItemStack stackInSlot = slot.getItem();
    ItemStack copy = stackInSlot.copy();

    if (index < CONTAINER_SLOTS) {
      if (!this.moveItemStackTo(stackInSlot, CONTAINER_SLOTS, this.slots.size(), true)) {
        return ItemStack.EMPTY;
      }
    } else {
      if (!this.moveItemStackTo(stackInSlot, 0, CONTAINER_SLOTS, false)) {
        return ItemStack.EMPTY;
      }
    }

    if (stackInSlot.isEmpty()) {
      slot.set(ItemStack.EMPTY);
    } else {
      slot.setChanged();
    }

    return copy;
  }

  @Override
  public void removed(Player player) {
    super.removed(player);
    this.container.stopOpen(player);
  }
}
