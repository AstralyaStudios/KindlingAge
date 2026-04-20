package net.astralya.kindlingage.neoforge.datagen.custom;

import java.util.LinkedHashMap;
import java.util.Map;
import net.astralya.kindlingage.recipe.custom.DryingRackRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class DryingRackRecipeBuilder implements RecipeBuilder {
  // Mirrors the vanilla recipe builder style for the custom drying rack serializer.
  private final RecipeCategory category;
  private final Ingredient ingredient;
  private final Item result;
  private final int count;
  private final int duration;
  private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

  private String group;

  private DryingRackRecipeBuilder(
      RecipeCategory category, Ingredient ingredient, ItemLike result, int count, int duration) {
    this.category = category;
    this.ingredient = ingredient;
    this.result = result.asItem();
    this.count = count;
    this.duration = duration;
  }

  public static DryingRackRecipeBuilder drying(
      RecipeCategory category, Ingredient ingredient, ItemLike result, int duration) {
    return new DryingRackRecipeBuilder(category, ingredient, result, 1, duration);
  }

  public static DryingRackRecipeBuilder drying(
      RecipeCategory category, Ingredient ingredient, ItemLike result, int count, int duration) {
    return new DryingRackRecipeBuilder(category, ingredient, result, count, duration);
  }

  @Override
  public DryingRackRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
    criteria.put(name, criterion);
    return this;
  }

  @Override
  public DryingRackRecipeBuilder group(String recipeGroup) {
    this.group = recipeGroup;
    return this;
  }

  @Override
  public Item getResult() {
    return result;
  }

  @Override
  public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
    ensureValid(recipeId);

    Advancement.Builder advancement =
        recipeOutput
            .advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
            .rewards(AdvancementRewards.Builder.recipe(recipeId));

    criteria.forEach(advancement::addCriterion);

    recipeOutput.accept(
        recipeId,
        new DryingRackRecipe(ingredient, new ItemStack(result, count), duration),
        advancement.build(recipeId.withPrefix("recipes/" + category.getFolderName() + "/")));
  }

  private void ensureValid(ResourceLocation recipeId) {
    if (criteria.isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + recipeId);
    }
  }
}
