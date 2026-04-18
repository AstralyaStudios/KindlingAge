package net.astralya.kindlingage.item.custom;

import net.astralya.kindlingage.screen.CrudeBagMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class CrudeBagItem extends Item {
  private static final Component TITLE = Component.translatable("container.kindlingage.crude_bag");

  public CrudeBagItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);

    if (!level.isClientSide) {
      level.playSound(
          null,
          player.blockPosition(),
          SoundEvents.ARMOR_EQUIP_LEATHER.value(),
          SoundSource.PLAYERS,
          0.8F,
          1.0F);

      player.openMenu(
          new SimpleMenuProvider(
              (containerId, inventory, menuPlayer) ->
                  new CrudeBagMenu(containerId, inventory, menuPlayer, hand),
              TITLE));
    }

    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
  }
}
