package net.astralya.kindlingage.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class CrudeBagInventory implements Container {
  public static final int SIZE = 9;

  private final ItemStack bagStack;
  private final HolderLookup.Provider lookup;
  private final NonNullList<ItemStack> items;

  public CrudeBagInventory(ItemStack bagStack, HolderLookup.Provider lookup) {
    this.bagStack = bagStack;
    this.lookup = lookup;
    this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
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
    for (int i = 0; i < SIZE; i++) {
      items.add(ItemStack.EMPTY);
    }
    setChanged();
  }

  private void loadFromStack() {
    CompoundTag root = getOrCreateRootTag();
    if (!root.contains("Items")) {
      return;
    }

    ListTag list = root.getList("Items", CompoundTag.TAG_COMPOUND);
    for (int i = 0; i < list.size(); i++) {
      CompoundTag entry = list.getCompound(i);
      int slot = entry.getInt("Slot");

      if (slot >= 0 && slot < items.size()) {
        ItemStack.parse(lookup, entry.getCompound("Item"))
            .ifPresent(stack -> items.set(slot, stack));
      }
    }
  }

  private void saveToStack() {
    CompoundTag root = getOrCreateRootTag();
    ListTag list = new ListTag();

    for (int i = 0; i < items.size(); i++) {
      ItemStack stack = items.get(i);
      if (!stack.isEmpty()) {
        CompoundTag entry = new CompoundTag();
        entry.putInt("Slot", i);
        entry.put("Item", stack.save(lookup));
        list.add(entry);
      }
    }

    root.put("Items", list);
    setRootTag(root);
  }

  private CompoundTag getOrCreateRootTag() {
    CustomData data = bagStack.get(DataComponents.CUSTOM_DATA);
    return data != null ? data.copyTag() : new CompoundTag();
  }

  private void setRootTag(CompoundTag tag) {
    bagStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
  }
}
