package net.astralya.kindlingage.neoforge.datagen;

import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, KindlingAge.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.FLINT_FLAKE.get());
        basicItem(ModItems.PLANT_FIBER.get());
        basicItem(ModItems.FIBER_CORD.get());
        basicItem(ModItems.FLINT_KNIFE.get());
        basicItem(ModItems.FLINT_HATCHET.get());
        handheldItem(ModItems.HUNTING_SPEAR.get());
        basicItem(ModItems.REED_SCOOP.get());
        basicItem(ModItems.CRUDE_BAG.get());
        basicItem(ModItems.WATER_REED_SCOOP.get());
        basicItem(ModItems.REED_MAT.get());
    }
}