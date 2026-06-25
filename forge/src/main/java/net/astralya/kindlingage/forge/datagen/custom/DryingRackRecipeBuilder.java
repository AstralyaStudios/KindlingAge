package net.astralya.kindlingage.forge.datagen.custom;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.astralya.kindlingage.recipe.ModRecipeTypes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class DryingRackRecipeBuilder implements RecipeBuilder {
  private final RecipeCategory category;
  private final Ingredient ingredient;
  private final Item result;
  private final int count;
  private final int duration;
  private final Advancement.Builder advancement = Advancement.Builder.advancement();

  private @Nullable String group;

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

  @Override
  public DryingRackRecipeBuilder unlockedBy(
      String name, CriterionTriggerInstance criterionTrigger) {
    advancement.addCriterion(name, criterionTrigger);
    return this;
  }

  @Override
  public DryingRackRecipeBuilder group(@Nullable String recipeGroup) {
    this.group = recipeGroup;
    return this;
  }

  @Override
  public Item getResult() {
    return result;
  }

  @Override
  public void save(Consumer<FinishedRecipe> writer, ResourceLocation recipeId) {
    ensureValid(recipeId);
    advancement
        .parent(ROOT_RECIPE_ADVANCEMENT)
        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
        .rewards(AdvancementRewards.Builder.recipe(recipeId))
        .requirements(RequirementsStrategy.OR);
    writer.accept(new Result(recipeId, category, group, ingredient, new ItemStack(result, count), duration, advancement));
  }

  private void ensureValid(ResourceLocation recipeId) {
    if (advancement.getCriteria().isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + recipeId);
    }
  }

  private record Result(
      ResourceLocation id,
      RecipeCategory category,
      @Nullable String group,
      Ingredient ingredient,
      ItemStack result,
      int duration,
      Advancement.Builder advancement)
      implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject json) {
      if (group != null && !group.isEmpty()) {
        json.addProperty("group", group);
      }

      json.add("ingredient", ingredient.toJson());
      JsonObject resultJson = new JsonObject();
      resultJson.addProperty(
          "item", ForgeRegistries.ITEMS.getKey(result.getItem()).toString());
      if (result.getCount() != 1) {
        resultJson.addProperty("count", result.getCount());
      }
      json.add("result", resultJson);
      json.addProperty("duration", duration);
    }

    @Override
    public ResourceLocation getId() {
      return id;
    }

    @Override
    public net.minecraft.world.item.crafting.RecipeSerializer<?> getType() {
      return ModRecipeTypes.DRYING_SERIALIZER.get();
    }

    @Override
    public @Nullable JsonObject serializeAdvancement() {
      return advancement.serializeToJson();
    }

    @Override
    public @Nullable ResourceLocation getAdvancementId() {
      return id.withPrefix("recipes/" + category.getFolderName() + "/");
    }
  }
}
