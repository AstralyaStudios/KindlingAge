package net.astralya.kindlingage.neoforge.datagen;

import net.astralya.kindlingage.KindlingAge;
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
        add("container.kindlingage.wicker_basket", "Wicker Basket");
        add("container.kindlingage.crude_bag", "Crude Bag");
        add("itemGroup.kindlingage", "Kindling Age");
    }
}