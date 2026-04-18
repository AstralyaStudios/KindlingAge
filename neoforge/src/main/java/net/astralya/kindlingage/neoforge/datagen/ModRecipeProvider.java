package net.astralya.kindlingage.neoforge.datagen;

import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public final class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FLINT_FLAKE.get(), 4)
                .requires(Items.FLINT)
                .unlockedBy("has_flint",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.FLINT).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FIBER_CORD.get())
                .requires(ModItems.PLANT_FIBER.get())
                .requires(ModItems.PLANT_FIBER.get())
                .requires(ModItems.PLANT_FIBER.get())
                .unlockedBy("has_plant_fiber",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.PLANT_FIBER.get()).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FLINT_KNIFE.get())
                .pattern(" F")
                .pattern("S ")
                .define('F', ModItems.FLINT_FLAKE.get())
                .define('S', Items.STICK)
                .unlockedBy("has_flint_flake",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FLINT_FLAKE.get()).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FLINT_HATCHET.get())
                .pattern("FF")
                .pattern("SC")
                .define('F', ModItems.FLINT_FLAKE.get())
                .define('S', Items.STICK)
                .define('C', ModItems.FIBER_CORD.get())
                .unlockedBy("has_flint_flake", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FLINT_FLAKE.get()).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HUNTING_SPEAR.get())
                .pattern("  F")
                .pattern(" SC")
                .pattern("S  ")
                .define('F', ModItems.FLINT_FLAKE.get())
                .define('S', Items.STICK)
                .define('C', ModItems.FIBER_CORD.get())
                .unlockedBy("has_flint_flake", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FLINT_FLAKE.get()).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.REED_SCOOP.get())
                .pattern("R R")
                .pattern(" C ")
                .define('R', Items.SUGAR_CANE)
                .define('C', ModItems.FIBER_CORD.get())
                .unlockedBy("has_fiber_cord", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FIBER_CORD.get()).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WET_CLAY_POT.get())
                .pattern("C C")
                .pattern(" C ")
                .define('C', Items.CLAY_BALL)
                .unlockedBy("has_clay_ball", inventoryTrigger(ItemPredicate.Builder.item().of(Items.CLAY_BALL).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.REED_MAT.get())
                .pattern("PPP")
                .pattern("CCC")
                .define('P', ModItems.PLANT_FIBER.get())
                .define('C', ModItems.FIBER_CORD.get())
                .unlockedBy("has_fiber_cord", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FIBER_CORD.get()).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.WICKER_BASKET.get())
                .pattern("C C")
                .pattern("CCC")
                .define('C', ModItems.FIBER_CORD.get())
                .unlockedBy("has_fiber_cord", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.FIBER_CORD.get()).build()))
                .save(recipeOutput);

        // Cooking Recipes
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.WET_CLAY_POT.get().asItem()),
                        RecipeCategory.MISC,
                        ModBlocks.CLAY_POT.get(),
                        0.1f,
                        200
                )
                .unlockedBy("has_wet_clay_pot",
                        inventoryTrigger(ItemPredicate.Builder.item()
                                .of(ModBlocks.WET_CLAY_POT.get().asItem())
                                .build()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("kindlingage", "clay_pot_from_smelting"));

        SimpleCookingRecipeBuilder.campfireCooking(
                        Ingredient.of(ModBlocks.WET_CLAY_POT.get().asItem()),
                        RecipeCategory.MISC,
                        ModBlocks.CLAY_POT.get(),
                        0.1f,
                        600
                )
                .unlockedBy("has_wet_clay_pot",
                        inventoryTrigger(ItemPredicate.Builder.item()
                                .of(ModBlocks.WET_CLAY_POT.get().asItem())
                                .build()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("kindlingage", "clay_pot_from_campfire"));
    }
}