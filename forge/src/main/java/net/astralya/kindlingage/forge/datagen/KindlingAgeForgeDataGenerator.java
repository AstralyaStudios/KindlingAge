package net.astralya.kindlingage.forge.datagen;

import java.util.concurrent.CompletableFuture;
import net.astralya.kindlingage.KindlingAge;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KindlingAge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class KindlingAgeForgeDataGenerator {
  private KindlingAgeForgeDataGenerator() {}

  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = generator.getPackOutput();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

    ModBlockTagProvider blockTags =
        new ModBlockTagProvider(output, registries, existingFileHelper);

    generator.addProvider(event.includeClient(), new ModItemModelProvider(output, existingFileHelper));
    generator.addProvider(event.includeClient(), new ModLanguageProvider(output));
    generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, existingFileHelper));
    generator.addProvider(event.includeServer(), new ModRecipeProvider(output));
    generator.addProvider(event.includeServer(), new ModBlockLootTableProvider(output));
    generator.addProvider(
        event.includeServer(), new ModAdvancementProvider(output, registries, existingFileHelper));
    generator.addProvider(event.includeServer(), blockTags);
    generator.addProvider(
        event.includeServer(),
        new ModItemTagProvider(output, registries, blockTags.contentsGetter(), existingFileHelper));
    generator.addProvider(event.includeServer(), new ModEntityTypeTagProvider(output, registries));
  }
}
