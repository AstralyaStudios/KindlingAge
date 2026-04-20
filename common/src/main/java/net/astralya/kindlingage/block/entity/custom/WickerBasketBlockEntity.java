package net.astralya.kindlingage.block.entity.custom;

import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.screen.WickerBasketMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

public final class WickerBasketBlockEntity extends BlockEntity implements Container, MenuProvider {
  public static final int COLUMNS = 9;
  public static final int ROWS = 2;
  public static final int SIZE = COLUMNS * ROWS;

  private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

  private final ContainerOpenersCounter openersCounter =
      new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
          level.playSound(
              null,
              pos,
              SoundEvents.GRASS_STEP,
              SoundSource.BLOCKS,
              0.5F,
              level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
          level.playSound(
              null,
              pos,
              SoundEvents.GRASS_STEP,
              SoundSource.BLOCKS,
              0.5F,
              level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(
            Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {}

        @Override
        protected boolean isOwnContainer(Player player) {
          return player.containerMenu instanceof WickerBasketMenu menu
              && menu.getContainer() == WickerBasketBlockEntity.this;
        }
      };

  public WickerBasketBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.WICKER_BASKET.get(), pos, state);
  }

  public static void serverTick(
      Level level, BlockPos pos, BlockState state, WickerBasketBlockEntity blockEntity) {
    blockEntity.openersCounter.recheckOpeners(level, pos, state);
  }

  @Override
  public void startOpen(Player player) {
    if (this.level == null || this.isRemoved() || player.isSpectator()) {
      return;
    }

    this.openersCounter.incrementOpeners(
        player, this.level, this.worldPosition, this.getBlockState());
  }

  @Override
  public void stopOpen(Player player) {
    if (this.level == null || this.isRemoved() || player.isSpectator()) {
      return;
    }

    this.openersCounter.decrementOpeners(
        player, this.level, this.worldPosition, this.getBlockState());
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
    if (this.level == null) {
      return false;
    }

    if (this.level.getBlockEntity(this.worldPosition) != this) {
      return false;
    }

    double dx = player.getX() - (this.worldPosition.getX() + 0.5D);
    double dy = player.getY() - (this.worldPosition.getY() + 0.5D);
    double dz = player.getZ() - (this.worldPosition.getZ() + 0.5D);
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
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    ContainerHelper.loadAllItems(tag, this.items, registries);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    ContainerHelper.saveAllItems(tag, this.items, registries);
  }
}
