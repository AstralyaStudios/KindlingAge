package net.astralya.kindlingage.neoforge.datagen;

import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.item.ModItems;
import net.astralya.kindlingage.neoforge.datagen.custom.DryingRackRecipeBuilder;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public final class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
  public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput recipeOutput) {
    // Basic crafting and block recipes.
    buildCraftingRecipes(recipeOutput);

    // Vanilla-style heat processing recipes.
    buildCookingRecipes(recipeOutput);

    // Custom drying rack recipes with per-item durations.
    buildDryingRecipes(recipeOutput);
  }

  private void buildCraftingRecipes(RecipeOutput recipeOutput) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FLINT_FLAKE.get(), 4)
        .requires(Items.FLINT)
        .unlockedBy("has_flint", inventoryTrigger(ItemPredicate.Builder.item().of(Items.FLINT).build()))
        .save(recipeOutput);

    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FIBER_CORD.get())
        .requires(ModItems.PLANT_FIBER.get())
        .requires(ModItems.PLANT_FIBER.get())
        .requires(ModItems.PLANT_FIBER.get())
        .unlockedBy(
            "has_plant_fiber",
            inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.PLANT_FIBER.get()).build()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FLINT_KNIFE.get())
        .pattern(" F")
        .pattern("S ")
        .define('F', ModItems.FLINT_FLAKE.get())
        .define('S', Items.STICK)
        .unlockedBy(
            "has_flint_flake",
            inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FLINT_FLAKE.get()).build()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FLINT_HATCHET.get())
        .pattern("FF")
        .pattern("SC")
        .define('F', ModItems.FLINT_FLAKE.get())
        .define('S', Items.STICK)
        .define('C', ModItems.FIBER_CORD.get())
        .unlockedBy(
            "has_flint_flake",
            inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FLINT_FLAKE.get()).build()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HUNTING_SPEAR.get())
        .pattern("  F")
        .pattern(" SC")
        .pattern("S  ")
        .define('F', ModItems.FLINT_FLAKE.get())
        .define('S', Items.STICK)
        .define('C', ModItems.FIBER_CORD.get())
        .unlockedBy(
            "has_flint_flake",
            inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FLINT_FLAKE.get()).build()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.REED_SCOOP.get())
        .pattern("R R")
        .pattern(" C ")
        .define('R', Items.SUGAR_CANE)
        .define('C', ModItems.FIBER_CORD.get())
        .unlockedBy(
            "has_fiber_cord",
            inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FIBER_CORD.get()).build()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.WET_CLAY_POT.get())
        .pattern("C C")
        .pattern(" C ")
        .define('C', Items.CLAY_BALL)
        .unlockedBy(
            "has_clay_ball", inventoryTrigger(ItemPredicate.Builder.item().of(Items.CLAY_BALL).build()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.REED_MAT.get())
        .pattern("PPP")
        .pattern("CCC")
        .define('P', ModItems.PLANT_FIBER.get())
        .define('C', ModItems.FIBER_CORD.get())
        .unlockedBy(
            "has_fiber_cord",
            inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FIBER_CORD.get()).build()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WICKER_BASKET.get())
        .pattern("C C")
        .pattern("CCC")
        .define('C', ModItems.FIBER_CORD.get())
        .unlockedBy(
            "has_fiber_cord",
            inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FIBER_CORD.get()).build()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.CRUDE_BAG.get())
        .pattern("LCL")
        .pattern(" L ")
        .define('C', ModItems.FIBER_CORD.get())
        .define('L', Items.LEATHER)
        .unlockedBy(
            "has_fiber_cord",
            inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FIBER_CORD.get()).build()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DRYING_RACK.get())
        .pattern("SSS")
        .pattern("S S")
        .pattern("S S")
        .define('S', Items.STICK)
        .unlockedBy("has_stick", inventoryTrigger(ItemPredicate.Builder.item().of(Items.STICK).build()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FISH_TRAP.get())
        .pattern("SCS")
        .pattern("C C")
        .pattern("SSS")
        .define('S', Items.STICK)
        .define('C', Items.SUGAR_CANE)
        .unlockedBy("has_stick", inventoryTrigger(ItemPredicate.Builder.item().of(Items.STICK).build()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.HEARTH_PIT.get())
        .pattern("CKC")
        .pattern("CCC")
        .define('K', ModItems.KINDLING.get())
        .define('C', Items.COBBLESTONE)
        .unlockedBy(
            "has_kindling",
            inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.KINDLING.get()).build()))
        .save(recipeOutput);

    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.KINDLING.get(), 1)
        .requires(Items.STICK)
        .requires(ModItems.PLANT_FIBER.get())
        .unlockedBy(
            "has_plant_fiber",
            inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.PLANT_FIBER.get()).build()))
        .save(recipeOutput);
  }

  private void buildCookingRecipes(RecipeOutput recipeOutput) {
    SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(ModItems.WILD_SEEDS.get()),
            RecipeCategory.FOOD,
            ModItems.ROASTED_SEEDS.get(),
            0.1f,
            200)
        .unlockedBy(
            "has_wild_seeds",
            inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.WILD_SEEDS.get()).build()))
        .save(
            recipeOutput,
            ResourceLocation.fromNamespaceAndPath("kindlingage", "roasted_seeds_from_smelting"));

    SimpleCookingRecipeBuilder.campfireCooking(
            Ingredient.of(ModItems.WILD_SEEDS.get()),
            RecipeCategory.FOOD,
            ModItems.ROASTED_SEEDS.get(),
            0.1f,
            600)
        .unlockedBy(
            "has_wild_seeds",
            inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.WILD_SEEDS.get()).build()))
        .save(
            recipeOutput,
            ResourceLocation.fromNamespaceAndPath("kindlingage", "roasted_seeds_from_campfire"));

    SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(ModBlocks.WET_CLAY_POT.get().asItem()),
            RecipeCategory.MISC,
            ModBlocks.CLAY_POT.get(),
            0.1f,
            200)
        .unlockedBy(
            "has_wet_clay_pot",
            inventoryTrigger(
                ItemPredicate.Builder.item().of(ModBlocks.WET_CLAY_POT.get().asItem()).build()))
        .save(
            recipeOutput,
            ResourceLocation.fromNamespaceAndPath("kindlingage", "clay_pot_from_smelting"));

    SimpleCookingRecipeBuilder.campfireCooking(
            Ingredient.of(ModBlocks.WET_CLAY_POT.get().asItem()),
            RecipeCategory.MISC,
            ModBlocks.CLAY_POT.get(),
            0.1f,
            600)
        .unlockedBy(
            "has_wet_clay_pot",
            inventoryTrigger(
                ItemPredicate.Builder.item().of(ModBlocks.WET_CLAY_POT.get().asItem()).build()))
        .save(
            recipeOutput,
            ResourceLocation.fromNamespaceAndPath("kindlingage", "clay_pot_from_campfire"));
  }

  private void buildDryingRecipes(RecipeOutput recipeOutput) {
    DryingRackRecipeBuilder.drying(
            RecipeCategory.FOOD,
            Ingredient.of(Tags.Items.FOODS_RAW_FISH),
            ModItems.DRIED_FISH.get(),
            4800)
        .unlockedBy(
            "has_raw_fish",
            inventoryTrigger(ItemPredicate.Builder.item().of(Tags.Items.FOODS_RAW_FISH).build()))
        .save(
            recipeOutput,
            ResourceLocation.fromNamespaceAndPath("kindlingage", "raw_fish_to_dried_fish_drying"));

    DryingRackRecipeBuilder.drying(
            RecipeCategory.FOOD,
            Ingredient.of(Tags.Items.FOODS_RAW_MEAT),
            ModItems.JERKY.get(),
            4800)
        .unlockedBy(
            "has_raw_meat",
            inventoryTrigger(ItemPredicate.Builder.item().of(Tags.Items.FOODS_RAW_MEAT).build()))
        .save(
            recipeOutput,
            ResourceLocation.fromNamespaceAndPath("kindlingage", "raw_meat_to_jerky_drying"));

    DryingRackRecipeBuilder.drying(
            RecipeCategory.FOOD, Ingredient.of(Items.KELP), Items.DRIED_KELP, 2400)
        .unlockedBy("has_kelp", inventoryTrigger(ItemPredicate.Builder.item().of(Items.KELP).build()))
        .save(
            recipeOutput,
            ResourceLocation.fromNamespaceAndPath("kindlingage", "kelp_to_dried_kelp_drying"));

    DryingRackRecipeBuilder.drying(
            RecipeCategory.MISC, Ingredient.of(Items.ROTTEN_FLESH), Items.LEATHER, 9600)
        .unlockedBy(
            "has_rotten_flesh",
            inventoryTrigger(ItemPredicate.Builder.item().of(Items.ROTTEN_FLESH).build()))
        .save(
            recipeOutput,
            ResourceLocation.fromNamespaceAndPath("kindlingage", "rotten_flesh_to_leather_drying"));
  }
}
