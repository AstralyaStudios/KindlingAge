package net.astralya.mixin;

import net.astralya.kindlingage.KindlingAgeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerMixin {
  @Inject(method = "blockActionRestricted", at = @At("HEAD"), cancellable = true)
  private void kindlingage$preventHandBreakingLogs(
      Level level, BlockPos pos, GameType gameType, CallbackInfoReturnable<Boolean> cir) {
    Player player = (Player) (Object) this;
    if (!KindlingAgeConfig.preventHandBreakingLogs()) {
      return;
    }

    if (player.getAbilities().instabuild) {
      return;
    }

    if (!player.getMainHandItem().isEmpty()) {
      return;
    }

    if (level.getBlockState(pos).is(BlockTags.LOGS)) {
      cir.setReturnValue(true);
    }
  }
}
