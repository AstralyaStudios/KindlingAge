package net.astralya.kindlingage.forge.datagen;

import java.util.concurrent.CompletableFuture;
import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public final class ModBlockTagProvider extends BlockTagsProvider {
  public ModBlockTagProvider(
      PackOutput output,
      CompletableFuture<HolderLookup.Provider> lookupProvider,
      @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, KindlingAge.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(ModTags.Blocks.FLINT_KNIFE_FIBER_SOURCES)
        .add(Blocks.GRASS, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN);

    tag(ModTags.Blocks.FLINT_KNIFE_WILD_SEED_SOURCES).addTag(BlockTags.SMALL_FLOWERS);

    tag(ModTags.Blocks.FLINT_KNIFE_STICK_SOURCES).addTag(BlockTags.LEAVES);

    tag(BlockTags.MINEABLE_WITH_AXE)
        .add(
            ModBlocks.WICKER_BASKET.get(),
            ModBlocks.FISH_TRAP.get(),
            ModBlocks.DRYING_RACK.get());

    tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .add(ModBlocks.CLAY_POT.get(), ModBlocks.HEARTH_PIT.get());

    tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.WET_CLAY_POT.get());
  }
}
