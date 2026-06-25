package net.astralya.kindlingage.block.entity.custom;

import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.screen.WickerBasketMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class WickerBasketBlockEntity extends BlockEntity implements Container, MenuProvider {
  public static final int COLUMNS = 9;
  public static final int ROWS = 2;
  public static final int SIZE = COLUMNS * ROWS;

  private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

  public WickerBasketBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.WICKER_BASKET.get(), pos, state);
  }

  @Override
  public int getContainerSize() {
    return items.size();
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
    ItemStack result = ContainerHelper.removeItem(items, slot, amount);
    if (!result.isEmpty()) {
      setChanged();
    }
    return result;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    ItemStack result = ContainerHelper.takeItem(items, slot);
    if (!result.isEmpty()) {
      setChanged();
    }
    return result;
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    items.set(slot, stack);
    int max = getMaxStackSize();
    if (stack.getCount() > max) {
      stack.setCount(max);
    }
    setChanged();
  }

  @Override
  public boolean stillValid(Player player) {
    if (level == null || level.getBlockEntity(worldPosition) != this) {
      return false;
    }

    double dx = player.getX() - (worldPosition.getX() + 0.5D);
    double dy = player.getY() - (worldPosition.getY() + 0.5D);
    double dz = player.getZ() - (worldPosition.getZ() + 0.5D);
    return (dx * dx + dy * dy + dz * dz) <= 64.0D;
  }

  @Override
  public void clearContent() {
    items.clear();
    setChanged();
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("container.kindlingage.wicker_basket");
  }

  @Override
  public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
    return new WickerBasketMenu(syncId, playerInventory, this);
  }

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    ContainerHelper.saveAllItems(tag, items);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    items.clear();
    ContainerHelper.loadAllItems(tag, items);
  }
}
