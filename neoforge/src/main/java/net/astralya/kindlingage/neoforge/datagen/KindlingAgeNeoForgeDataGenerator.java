package net.astralya.kindlingage.neoforge.datagen;

import net.astralya.kindlingage.KindlingAge;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = KindlingAge.MOD_ID)
public final class KindlingAgeNeoForgeDataGenerator {
    private KindlingAgeNeoForgeDataGenerator() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

        ModBlockTagProvider blockTags = new ModBlockTagProvider(output, registries, existingFileHelper);

        generator.addProvider(true, new ModItemModelProvider(output, existingFileHelper));
        generator.addProvider(true, new ModRecipeProvider(output, registries));
        generator.addProvider(true, new ModLanguageProvider(output));
        generator.addProvider(true, blockTags);
        generator.addProvider(true, new ModItemTagProvider(output, registries, blockTags.contentsGetter(), existingFileHelper));
        generator.addProvider(true, new ModEntityTypeTagProvider(output, registries));
        generator.addProvider(true, new ModBlockStateProvider(output, existingFileHelper));
        generator.addProvider(true, new ModBlockLootTableProvider(output, registries));
        generator.addProvider(true, new ModAdvancementProvider(output, registries, existingFileHelper));
    }
}
