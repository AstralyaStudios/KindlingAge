package net.astralya.kindlingage.forge.datagen;

import java.util.concurrent.CompletableFuture;
import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public final class ModItemTagProvider extends ItemTagsProvider {
  private static final TagKey<Item> TRIDENT_ENCHANTABLE = vanillaTag("enchantable/trident");
  private static final TagKey<Item> DURABILITY_ENCHANTABLE = vanillaTag("enchantable/durability");
  private static final TagKey<Item> MINING_ENCHANTABLE = vanillaTag("enchantable/mining");
  private static final TagKey<Item> MINING_LOOT_ENCHANTABLE = vanillaTag("enchantable/mining_loot");

  public ModItemTagProvider(
      PackOutput output,
      CompletableFuture<HolderLookup.Provider> lookupProvider,
      CompletableFuture<TagLookup<Block>> blockTags,
      @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, blockTags, KindlingAge.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(TRIDENT_ENCHANTABLE).add(ModItems.HUNTING_SPEAR.get());

    tag(DURABILITY_ENCHANTABLE)
        .add(ModItems.HUNTING_SPEAR.get())
        .add(ModItems.FLINT_HATCHET.get())
        .add(ModItems.FLINT_KNIFE.get());

    tag(MINING_ENCHANTABLE).add(ModItems.FLINT_HATCHET.get());

    tag(MINING_LOOT_ENCHANTABLE).add(ModItems.FLINT_HATCHET.get());
  }

  private static TagKey<Item> vanillaTag(String path) {
    return TagKey.create(Registries.ITEM, new ResourceLocation("minecraft", path));
  }
}
