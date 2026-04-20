package net.astralya.kindlingage.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.recipe.custom.DryingRackRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipeTypes {
  public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
      DeferredRegister.create(KindlingAge.MOD_ID, Registries.RECIPE_SERIALIZER);
  public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
      DeferredRegister.create(KindlingAge.MOD_ID, Registries.RECIPE_TYPE);

  public static final RegistrySupplier<RecipeSerializer<DryingRackRecipe>> DRYING_SERIALIZER =
      RECIPE_SERIALIZERS.register("drying", DryingRackRecipe.Serializer::new);

  public static final RegistrySupplier<RecipeType<DryingRackRecipe>> DRYING =
      RECIPE_TYPES.register(
          "drying",
          () ->
              new RecipeType<>() {
                @Override
                public String toString() {
                  return KindlingAge.MOD_ID + ":drying";
                }
              });

  private ModRecipeTypes() {}

  public static void init() {
    RECIPE_SERIALIZERS.register();
    RECIPE_TYPES.register();
  }
}
