package net.astralya.kindlingage.recipe.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.recipe.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public record DryingRackRecipe(Ingredient ingredient, ItemStack result, int duration)
    implements Recipe<SingleRecipeInput> {
  @Override
  public boolean matches(SingleRecipeInput input, net.minecraft.world.level.Level level) {
    return ingredient.test(input.item());
  }

  @Override
  public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
    return result.copy();
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return true;
  }

  @Override
  public ItemStack getResultItem(HolderLookup.Provider registries) {
    return result.copy();
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
  public boolean showNotification() {
    return false;
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
    private static final MapCodec<DryingRackRecipe> CODEC =
        RecordCodecBuilder.mapCodec(
            instance ->
                instance
                    .group(
                        Ingredient.CODEC_NONEMPTY
                            .fieldOf("ingredient")
                            .forGetter(DryingRackRecipe::ingredient),
                        ItemStack.STRICT_CODEC
                            .fieldOf("result")
                            .forGetter(DryingRackRecipe::result),
                        Codec.intRange(1, Integer.MAX_VALUE)
                            .fieldOf("duration")
                            .forGetter(DryingRackRecipe::duration))
                    .apply(instance, DryingRackRecipe::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, DryingRackRecipe> STREAM_CODEC =
        StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            DryingRackRecipe::ingredient,
            ItemStack.STREAM_CODEC,
            DryingRackRecipe::result,
            ByteBufCodecs.VAR_INT,
            DryingRackRecipe::duration,
            DryingRackRecipe::new);

    @Override
    public MapCodec<DryingRackRecipe> codec() {
      return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DryingRackRecipe> streamCodec() {
      return STREAM_CODEC;
    }
  }
}
