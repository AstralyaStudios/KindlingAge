package net.astralya.kindlingage.recipe.custom;

import com.google.gson.JsonObject;
import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.recipe.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record DryingRackRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, int duration)
    implements Recipe<Container> {
  @Override
  public boolean matches(Container container, Level level) {
    return ingredient.test(container.getItem(0));
  }

  @Override
  public ItemStack assemble(Container container, RegistryAccess registryAccess) {
    return result.copy();
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return true;
  }

  @Override
  public ItemStack getResultItem(RegistryAccess registryAccess) {
    return result.copy();
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.of(Ingredient.EMPTY, ingredient);
  }

  @Override
  public ItemStack getToastSymbol() {
    return new ItemStack(ModBlocks.DRYING_RACK.get());
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipeTypes.DRYING_SERIALIZER.get();
  }

  @Override
  public RecipeType<?> getType() {
    return ModRecipeTypes.DRYING.get();
  }

  public static final class Serializer implements RecipeSerializer<DryingRackRecipe> {
    @Override
    public DryingRackRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
      Ingredient ingredient = Ingredient.fromJson(GsonHelper.getNonNull(json, "ingredient"));
      ItemStack result = itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
      int duration = GsonHelper.getAsInt(json, "duration");
      return new DryingRackRecipe(recipeId, ingredient, result, duration);
    }

    @Override
    public @Nullable DryingRackRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
      Ingredient ingredient = Ingredient.fromNetwork(buffer);
      ItemStack result = buffer.readItem();
      int duration = buffer.readVarInt();
      return new DryingRackRecipe(recipeId, ingredient, result, duration);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, DryingRackRecipe recipe) {
      recipe.ingredient.toNetwork(buffer);
      buffer.writeItem(recipe.result);
      buffer.writeVarInt(recipe.duration);
    }

    private static ItemStack itemStackFromJson(JsonObject json) {
      String itemName =
          GsonHelper.isStringValue(json, "item")
              ? GsonHelper.getAsString(json, "item")
              : GsonHelper.getAsString(json, "id");
      int count = GsonHelper.getAsInt(json, "count", 1);
      return new ItemStack(
          BuiltInRegistries.ITEM.get(new ResourceLocation(itemName)), count);
    }
  }
}
