package net.astralya.kindlingage.neoforge.datagen;

import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, KindlingAge.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ModItems.FLINT_FLAKE.get(), "Flint Flake");
        add(ModItems.PLANT_FIBER.get(), "Plant Fiber");
        add(ModItems.FIBER_CORD.get(), "Fiber Cord");
        add(ModItems.WILD_SEEDS.get(), "Wild Seeds");
        add(ModItems.ROASTED_SEEDS.get(), "Roasted Seeds");
        add(ModItems.DRIED_FISH.get(), "Dried Fish");
        add(ModItems.JERKY.get(), "Jerky");
        add(ModItems.KINDLING.get(), "Kindling");
        add(ModItems.FLINT_KNIFE.get(), "Flint Knife");
        add(ModItems.FLINT_HATCHET.get(), "Flint Hatchet");
        add(ModItems.HUNTING_SPEAR.get(), "Hunting Spear");
        add(ModItems.REED_SCOOP.get(), "Reed Scoop");
        add(ModItems.CRUDE_BAG.get(), "Crude Bag");
        add(ModItems.WATER_REED_SCOOP.get(), "Water Reed Scoop");
        add(ModItems.REED_MAT.get(), "Reed Mat");
        add(ModItems.CLAY_POT.get(), "Clay Pot");
        add(ModItems.WET_CLAY_POT.get(), "Wet Clay Pot");
        add(ModItems.WICKER_BASKET.get(), "Wicker Basket");
        add(ModBlocks.HEARTH_PIT.get(), "Hearth Pit");
        add(ModBlocks.FISH_TRAP.get(), "Fish Trap");
        add(ModBlocks.DRYING_RACK.get(), "Drying Rack");
        add("container.kindlingage.wicker_basket", "Wicker Basket");
        add("container.kindlingage.crude_bag", "Crude Bag");
        add("itemGroup.kindlingage", "Kindling Age");

        add("advancements.kindlingage.root.title", "A Chip Off the Stone");
        add("advancements.kindlingage.root.description", "Pick up flint and begin a harsher road");
        add("advancements.kindlingage.spark.title", "Spark of the Idea");
        add("advancements.kindlingage.spark.description", "Bundle Kindling for your first real fire");
        add("advancements.kindlingage.gone_fishing.title", "Gone Fishing");
        add("advancements.kindlingage.gone_fishing.description", "Craft a Fish Trap and let the water work");
        add("advancements.kindlingage.sleeping_with_the_fishes.title", "Sleeping with the Fishes");
        add("advancements.kindlingage.sleeping_with_the_fishes.description", "Use Wild Seeds as bait in a Fish Trap");
        add("advancements.kindlingage.off_the_rack.title", "Off the Rack");
        add("advancements.kindlingage.off_the_rack.description", "Craft a Drying Rack for slow food and stranger cures");
        add("advancements.kindlingage.something_in_the_air.title", "Something in the Air");
        add("advancements.kindlingage.something_in_the_air.description", "Dry raw fish or meat into trail-ready food");
        add("advancements.kindlingage.leather_weather.title", "Leather Weather");
        add("advancements.kindlingage.leather_weather.description", "Turn Rotten Flesh into Leather on a Drying Rack");
        add("advancements.kindlingage.pot_luck.title", "Pot Luck");
        add("advancements.kindlingage.pot_luck.description", "Dry a Wet Clay Pot into a proper Clay Pot");
        add("advancements.kindlingage.water_you_waiting_for.title", "Water You Waiting For");
        add("advancements.kindlingage.water_you_waiting_for.description", "Fill a Reed Scoop and carry water by hand");
        add("advancements.kindlingage.where_theres_smoke.title", "Where There's Smoke");
        add("advancements.kindlingage.where_theres_smoke.description", "Set down a Hearth Pit for your first campfire meal");
        add("advancements.kindlingage.point_taken.title", "Point Taken");
        add("advancements.kindlingage.point_taken.description", "Craft a Hunting Spear fit for the early hunt");
        add("advancements.kindlingage.the_long_and_short_of_it.title", "The Long and Short of It");
        add("advancements.kindlingage.the_long_and_short_of_it.description", "Twist Plant Fiber into Fiber Cord");
        add("advancements.kindlingage.seed_you_later.title", "Seed You Later");
        add("advancements.kindlingage.seed_you_later.description", "Roast Wild Seeds before the road roasts you");
    }
}
