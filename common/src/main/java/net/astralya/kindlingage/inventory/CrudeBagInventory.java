package net.astralya.kindlingage.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class CrudeBagInventory implements Container {
  public static final int SIZE = 9;

  private final ItemStack bagStack;
  private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

  public CrudeBagInventory(ItemStack bagStack) {
    this.bagStack = bagStack;
    loadFromStack();
  }

  @Override
  public int getContainerSize() {
    return SIZE;
  }

  @Override
  public boolean isEmpty() {
    for (ItemStack stack : items) {
      if (!stack.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getItem(int slot) {
    return items.get(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    ItemStack existing = items.get(slot);
    if (existing.isEmpty()) {
      return ItemStack.EMPTY;
    }

    ItemStack split = existing.split(amount);
    if (!split.isEmpty()) {
      setChanged();
    }
    if (existing.isEmpty()) {
      items.set(slot, ItemStack.EMPTY);
    }
    return split;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    ItemStack existing = items.get(slot);
    if (existing.isEmpty()) {
      return ItemStack.EMPTY;
    }

    items.set(slot, ItemStack.EMPTY);
    return existing;
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    items.set(slot, stack);
    if (stack.getCount() > getMaxStackSize()) {
      stack.setCount(getMaxStackSize());
    }
    setChanged();
  }

  @Override
  public void setChanged() {
    saveToStack();
  }

  @Override
  public boolean stillValid(Player player) {
    return true;
  }

  @Override
  public void clearContent() {
    items.clear();
    for (int slot = 0; slot < SIZE; slot++) {
      items.set(slot, ItemStack.EMPTY);
    }
    setChanged();
  }

  private void loadFromStack() {
    CompoundTag root = bagStack.getOrCreateTag();
    if (!root.contains("Items", ListTag.TAG_LIST)) {
      return;
    }

    ListTag list = root.getList("Items", CompoundTag.TAG_COMPOUND);
    for (int i = 0; i < list.size(); i++) {
      CompoundTag entry = list.getCompound(i);
      int slot = entry.getInt("Slot");
      if (slot >= 0 && slot < items.size()) {
        items.set(slot, ItemStack.of(entry.getCompound("Item")));
      }
    }
  }

  private void saveToStack() {
    CompoundTag root = bagStack.getOrCreateTag();
    ListTag list = new ListTag();

    for (int slot = 0; slot < items.size(); slot++) {
      ItemStack stack = items.get(slot);
      if (!stack.isEmpty()) {
        CompoundTag entry = new CompoundTag();
        entry.putInt("Slot", slot);
        entry.put("Item", stack.save(new CompoundTag()));
        list.add(entry);
      }
    }

    root.put("Items", list);
  }
}
