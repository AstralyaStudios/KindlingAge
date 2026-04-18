package net.astralya.kindlingage.neoforge.datagen;

import net.astralya.kindlingage.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public final class ModEntityTypeTagProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.EntityTypes.SMALL_GAME)
                .add(
                        EntityType.CHICKEN,
                        EntityType.RABBIT,
                        EntityType.COD,
                        EntityType.SALMON
                );
    }
}