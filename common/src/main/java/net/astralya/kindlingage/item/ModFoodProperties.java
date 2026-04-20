package net.astralya.kindlingage.item;

import net.minecraft.world.food.FoodProperties;

public final class ModFoodProperties {
  public static final FoodProperties WILD_SEEDS =
      new FoodProperties.Builder().nutrition(1).saturationModifier(0.1f).build();

  public static final FoodProperties ROASTED_SEEDS =
      new FoodProperties.Builder().nutrition(2).saturationModifier(0.2f).build();

  public static final FoodProperties DRIED_FISH =
      new FoodProperties.Builder().nutrition(5).saturationModifier(0.7f).fast().build();

  public static final FoodProperties JERKY =
      new FoodProperties.Builder().nutrition(5).saturationModifier(0.7f).fast().build();

  private ModFoodProperties() {}
}
