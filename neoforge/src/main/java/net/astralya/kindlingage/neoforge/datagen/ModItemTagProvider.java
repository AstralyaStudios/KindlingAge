package net.astralya.kindlingage.neoforge.datagen;

import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public final class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<net.minecraft.world.level.block.Block>> blockTags,
            ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, blockTags, KindlingAge.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(ItemTags.TRIDENT_ENCHANTABLE)
                .add(ModItems.HUNTING_SPEAR.get());

        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ModItems.HUNTING_SPEAR.get())
                .add(ModItems.FLINT_HATCHET.get())
                .add(ModItems.FLINT_KNIFE.get());

        tag(ItemTags.MINING_ENCHANTABLE)
                .add(ModItems.FLINT_HATCHET.get());

        tag(ItemTags.MINING_LOOT_ENCHANTABLE)
                .add(ModItems.FLINT_HATCHET.get());
    }
}