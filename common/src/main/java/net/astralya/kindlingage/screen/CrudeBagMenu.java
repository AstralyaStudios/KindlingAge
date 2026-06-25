package net.astralya.kindlingage.screen;

import net.astralya.kindlingage.inventory.CrudeBagInventory;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class CrudeBagMenu extends AbstractContainerMenu {
  public static final int COLUMNS = 9;
  public static final int ROWS = 1;
  public static final int CONTAINER_SLOTS = COLUMNS * ROWS;

  private static final int PLAYER_INV_SLOTS = 27;
  private static final int HOTBAR_SLOTS = 9;

  private final Container container;
  private final InteractionHand openedHand;

  public CrudeBagMenu(int syncId, Inventory playerInventory) {
    this(syncId, playerInventory, playerInventory.player, resolveClientHand(playerInventory.player));
  }

  public CrudeBagMenu(int syncId, Inventory playerInventory, Player player, InteractionHand hand) {
    super(ModMenuTypes.CRUDE_BAG.get(), syncId);
    this.openedHand = hand;
    this.container = new CrudeBagInventory(player.getItemInHand(hand));

    checkContainerSize(container, CONTAINER_SLOTS);
    container.startOpen(playerInventory.player);

    int containerX = 8;
    int containerY = 18;
    for (int col = 0; col < COLUMNS; col++) {
      addSlot(new BagSlot(container, col, containerX + col * 18, containerY));
    }

    int playerInvX = 8;
    int playerInvY = 64;
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        int index = col + row * 9 + 9;
        addSlot(new Slot(playerInventory, index, playerInvX + col * 18, playerInvY + row * 18));
      }
    }

    int hotbarY = playerInvY + 58;
    for (int col = 0; col < 9; col++) {
      addSlot(new Slot(playerInventory, col, playerInvX + col * 18, hotbarY));
    }

    addDataSlots(new SimpleContainerData(0));
  }

  @Override
  public boolean stillValid(Player player) {
    return container.stillValid(player)
        && player.getItemInHand(openedHand).is(ModItems.CRUDE_BAG.get());
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    Slot slot = slots.get(index);
    if (!slot.hasItem()) {
      return ItemStack.EMPTY;
    }

    ItemStack stackInSlot = slot.getItem();
    if (stackInSlot.is(ModItems.CRUDE_BAG.get())) {
      return ItemStack.EMPTY;
    }

    ItemStack copy = stackInSlot.copy();
    int containerEnd = CONTAINER_SLOTS;
    int playerInvStart = containerEnd;
    int playerInvEnd = playerInvStart + PLAYER_INV_SLOTS;
    int hotbarEnd = playerInvEnd + HOTBAR_SLOTS;

    if (index < containerEnd) {
      if (!moveItemStackTo(stackInSlot, playerInvStart, hotbarEnd, true)) {
        return ItemStack.EMPTY;
      }
    } else if (!moveItemStackTo(stackInSlot, 0, containerEnd, false)) {
      return ItemStack.EMPTY;
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
    container.stopOpen(player);
  }

  private static InteractionHand resolveClientHand(Player player) {
    boolean mainHandBag = player.getMainHandItem().is(ModItems.CRUDE_BAG.get());
    boolean offHandBag = player.getOffhandItem().is(ModItems.CRUDE_BAG.get());
    return offHandBag && !mainHandBag ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
  }

  private static final class BagSlot extends Slot {
    private BagSlot(Container container, int slot, int x, int y) {
      super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
      return !stack.is(ModItems.CRUDE_BAG.get());
    }
  }
}
