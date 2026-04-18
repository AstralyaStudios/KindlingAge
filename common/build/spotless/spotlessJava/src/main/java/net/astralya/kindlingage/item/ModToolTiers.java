package net.astralya.kindlingage.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("SameParameterValue")
public final class ModToolTiers {
  public static final Tier FLINT =
      new Tier() {
        @Override
        public int getUses() {
          return midUses(Tiers.WOOD, Tiers.STONE);
        }

        @Override
        public float getSpeed() {
          return midSpeed(Tiers.WOOD, Tiers.STONE);
        }

        @Override
        public float getAttackDamageBonus() {
          return midDamageBonus(Tiers.WOOD, Tiers.STONE);
        }

        @Override
        public net.minecraft.tags.TagKey<Block> getIncorrectBlocksForDrops() {
          return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
          return midEnchantability(Tiers.WOOD, Tiers.STONE);
        }

        @Override
        public Ingredient getRepairIngredient() {
          return Ingredient.of(ModItems.FLINT_FLAKE.get());
        }
      };

  private ModToolTiers() {}

  private static int midUses(Tier a, Tier b) {
    return (a.getUses() + b.getUses()) / 2;
  }

  private static float midSpeed(Tier a, Tier b) {
    return (a.getSpeed() + b.getSpeed()) / 2.0f;
  }

  private static float midDamageBonus(Tier a, Tier b) {
    return (a.getAttackDamageBonus() + b.getAttackDamageBonus()) / 2.0f;
  }

  private static int midEnchantability(Tier a, Tier b) {
    return (a.getEnchantmentValue() + b.getEnchantmentValue()) / 2;
  }
}
