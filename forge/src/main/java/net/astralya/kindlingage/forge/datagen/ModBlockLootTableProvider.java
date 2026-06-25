package net.astralya.kindlingage.forge.datagen;

import java.util.List;
import java.util.Set;
import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.block.custom.ReedMatBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public final class ModBlockLootTableProvider extends LootTableProvider {
  public ModBlockLootTableProvider(PackOutput output) {
    super(
        output,
        Set.of(),
        List.of(new SubProviderEntry(ModBlockLootSubProvider::new, LootContextParamSets.BLOCK)));
  }

  private static final class ModBlockLootSubProvider extends BlockLootSubProvider {
    private static final List<Block> KNOWN_BLOCKS =
        List.of(
            ModBlocks.WICKER_BASKET.get(),
            ModBlocks.REED_MAT.get(),
            ModBlocks.DRYING_RACK.get(),
            ModBlocks.CLAY_POT.get(),
            ModBlocks.WET_CLAY_POT.get(),
            ModBlocks.HEARTH_PIT.get(),
            ModBlocks.FISH_TRAP.get());

    private ModBlockLootSubProvider() {
      super(Set.<Item>of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
      dropSelf(ModBlocks.WICKER_BASKET.get());
      add(
          ModBlocks.REED_MAT.get(),
          block -> createSinglePropConditionTable(block, ReedMatBlock.PART, BedPart.FOOT));
      dropSelf(ModBlocks.DRYING_RACK.get());
      dropSelf(ModBlocks.CLAY_POT.get());
      dropSelf(ModBlocks.WET_CLAY_POT.get());
      add(ModBlocks.HEARTH_PIT.get(), this::createHearthPitDrops);
      dropSelf(ModBlocks.FISH_TRAP.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
      return KNOWN_BLOCKS;
    }

    private LootTable.Builder createHearthPitDrops(Block block) {
      return LootTable.lootTable()
          .withPool(
              applyExplosionCondition(
                  Items.COBBLESTONE,
                  LootPool.lootPool()
                      .setRolls(ConstantValue.exactly(1.0F))
                      .add(
                          LootItem.lootTableItem(Items.COBBLESTONE)
                              .apply(
                                  SetItemCountFunction.setCount(
                                      UniformGenerator.between(2.0F, 4.0F))))));
    }
  }
}
