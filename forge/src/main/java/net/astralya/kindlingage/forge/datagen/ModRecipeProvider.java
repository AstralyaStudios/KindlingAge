package net.astralya.kindlingage.forge.datagen;

import java.util.function.Consumer;
import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.forge.datagen.custom.DryingRackRecipeBuilder;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public final class ModRecipeProvider extends RecipeProvider {
  public ModRecipeProvider(PackOutput output) {
    super(output);
  }

  @Override
  protected void buildRecipes(Consumer<FinishedRecipe> writer) {
    buildCraftingRecipes(writer);
    buildCookingRecipes(writer);
    buildDryingRecipes(writer);
  }

  private void buildCraftingRecipes(Consumer<FinishedRecipe> writer) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FLINT_FLAKE.get(), 4)
        .requires(Items.FLINT)
        .unlockedBy("has_flint", has(Items.FLINT))
        .save(writer);

    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FIBER_CORD.get())
        .requires(ModItems.PLANT_FIBER.get())
        .requires(ModItems.PLANT_FIBER.get())
        .requires(ModItems.PLANT_FIBER.get())
        .unlockedBy("has_plant_fiber", has(ModItems.PLANT_FIBER.get()))
        .save(writer);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FLINT_KNIFE.get())
        .pattern(" F")
        .pattern("S ")
        .define('F', ModItems.FLINT_FLAKE.get())
        .define('S', Items.STICK)
        .unlockedBy("has_flint_flake", has(ModItems.FLINT_FLAKE.get()))
        .save(writer);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FLINT_HATCHET.get())
        .pattern("FF")
        .pattern("SC")
        .define('F', ModItems.FLINT_FLAKE.get())
        .define('S', Items.STICK)
        .define('C', ModItems.FIBER_CORD.get())
        .unlockedBy("has_flint_flake", has(ModItems.FLINT_FLAKE.get()))
        .save(writer);

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HUNTING_SPEAR.get())
        .pattern("  F")
        .pattern(" SC")
        .pattern("S  ")
        .define('F', ModItems.FLINT_FLAKE.get())
        .define('S', Items.STICK)
        .define('C', ModItems.FIBER_CORD.get())
        .unlockedBy("has_flint_flake", has(ModItems.FLINT_FLAKE.get()))
        .save(writer);

    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.WET_CLAY_POT.get())
        .pattern("C C")
        .pattern(" C ")
        .define('C', Items.CLAY_BALL)
        .unlockedBy("has_clay_ball", has(Items.CLAY_BALL))
        .save(writer);

    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.REED_MAT.get())
        .pattern("PPP")
        .pattern("CCC")
        .define('P', ModItems.PLANT_FIBER.get())
        .define('C', ModItems.FIBER_CORD.get())
        .unlockedBy("has_fiber_cord", has(ModItems.FIBER_CORD.get()))
        .save(writer);

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WICKER_BASKET.get())
        .pattern("C C")
        .pattern("CCC")
        .define('C', ModItems.FIBER_CORD.get())
        .unlockedBy("has_fiber_cord", has(ModItems.FIBER_CORD.get()))
        .save(writer);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.REED_SCOOP.get())
        .pattern("R R")
        .pattern(" C ")
        .define('R', Items.SUGAR_CANE)
        .define('C', ModItems.FIBER_CORD.get())
        .unlockedBy("has_fiber_cord", has(ModItems.FIBER_CORD.get()))
        .save(writer);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.CRUDE_BAG.get())
        .pattern("LCL")
        .pattern(" L ")
        .define('L', Items.LEATHER)
        .define('C', ModItems.FIBER_CORD.get())
        .unlockedBy("has_fiber_cord", has(ModItems.FIBER_CORD.get()))
        .save(writer);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DRYING_RACK.get())
        .pattern("SSS")
        .pattern("S S")
        .pattern("S S")
        .define('S', Items.STICK)
        .unlockedBy("has_stick", has(Items.STICK))
        .save(writer);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FISH_TRAP.get())
        .pattern("SCS")
        .pattern("C C")
        .pattern("SSS")
        .define('S', Items.STICK)
        .define('C', Items.SUGAR_CANE)
        .unlockedBy("has_stick", has(Items.STICK))
        .save(writer);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.HEARTH_PIT.get())
        .pattern("CKC")
        .pattern("CCC")
        .define('K', ModItems.KINDLING.get())
        .define('C', Items.COBBLESTONE)
        .unlockedBy("has_kindling", has(ModItems.KINDLING.get()))
        .save(writer);

    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.KINDLING.get())
        .requires(Items.STICK)
        .requires(ModItems.PLANT_FIBER.get())
        .unlockedBy("has_plant_fiber", has(ModItems.PLANT_FIBER.get()))
        .save(writer);
  }

  private void buildCookingRecipes(Consumer<FinishedRecipe> writer) {
    SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(ModItems.WILD_SEEDS.get()),
            RecipeCategory.FOOD,
            ModItems.ROASTED_SEEDS.get(),
            0.1F,
            200)
        .unlockedBy("has_wild_seeds", has(ModItems.WILD_SEEDS.get()))
        .save(writer, id("roasted_seeds_from_smelting"));

    SimpleCookingRecipeBuilder.campfireCooking(
            Ingredient.of(ModItems.WILD_SEEDS.get()),
            RecipeCategory.FOOD,
            ModItems.ROASTED_SEEDS.get(),
            0.1F,
            600)
        .unlockedBy("has_wild_seeds", has(ModItems.WILD_SEEDS.get()))
        .save(writer, id("roasted_seeds_from_campfire"));

    SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(ModItems.WET_CLAY_POT.get()),
            RecipeCategory.MISC,
            ModItems.CLAY_POT.get(),
            0.1F,
            200)
        .unlockedBy("has_wet_clay_pot", has(ModItems.WET_CLAY_POT.get()))
        .save(writer, id("clay_pot_from_smelting"));

    SimpleCookingRecipeBuilder.campfireCooking(
            Ingredient.of(ModItems.WET_CLAY_POT.get()),
            RecipeCategory.MISC,
            ModItems.CLAY_POT.get(),
            0.1F,
            600)
        .unlockedBy("has_wet_clay_pot", has(ModItems.WET_CLAY_POT.get()))
        .save(writer, id("clay_pot_from_campfire"));
  }

  private void buildDryingRecipes(Consumer<FinishedRecipe> writer) {
    DryingRackRecipeBuilder.drying(
            RecipeCategory.FOOD,
            Ingredient.of(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH),
            ModItems.DRIED_FISH.get(),
            4800)
        .unlockedBy("has_raw_fish", has(Items.COD))
        .save(writer, id("raw_fish_to_dried_fish_drying"));

    DryingRackRecipeBuilder.drying(
            RecipeCategory.FOOD,
            Ingredient.of(Items.BEEF, Items.PORKCHOP, Items.MUTTON, Items.CHICKEN, Items.RABBIT),
            ModItems.JERKY.get(),
            4800)
        .unlockedBy("has_raw_meat", has(Items.BEEF))
        .save(writer, id("raw_meat_to_jerky_drying"));

    DryingRackRecipeBuilder.drying(
            RecipeCategory.FOOD, Ingredient.of(Items.KELP), Items.DRIED_KELP, 2400)
        .unlockedBy("has_kelp", has(Items.KELP))
        .save(writer, id("kelp_to_dried_kelp_drying"));

    DryingRackRecipeBuilder.drying(
            RecipeCategory.MISC, Ingredient.of(Items.ROTTEN_FLESH), Items.LEATHER, 9600)
        .unlockedBy("has_rotten_flesh", has(Items.ROTTEN_FLESH))
        .save(writer, id("rotten_flesh_to_leather_drying"));
  }

  private static ResourceLocation id(String path) {
    return new ResourceLocation(KindlingAge.MOD_ID, path);
  }
}
