package net.astralya.kindlingage.forge.datagen;

import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class ModItemModelProvider extends ItemModelProvider {
  public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, KindlingAge.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    basicItem(ModItems.FLINT_FLAKE.get());
    basicItem(ModItems.PLANT_FIBER.get());
    basicItem(ModItems.FIBER_CORD.get());
    basicItem(ModItems.WILD_SEEDS.get());
    basicItem(ModItems.ROASTED_SEEDS.get());
    basicItem(ModItems.DRIED_FISH.get());
    basicItem(ModItems.JERKY.get());
    basicItem(ModItems.KINDLING.get());
    basicItem(ModItems.FLINT_KNIFE.get());
    basicItem(ModItems.FLINT_HATCHET.get());
    handheldItem("hunting_spear");
    basicItem(ModItems.CRUDE_BAG.get());
    basicItem(ModItems.REED_SCOOP.get());
    basicItem(ModItems.WATER_REED_SCOOP.get());
    blockItem("reed_mat", "reed_mat_foot");

    blockItem("wicker_basket");
    blockItem("drying_rack");
    blockItem("clay_pot");
    blockItem("wet_clay_pot");
    blockItem("hearth_pit", "hearth_pit_empty");
    blockItem("fish_trap");
  }

  private void handheldItem(String name) {
    withExistingParent(name, mcLoc("item/handheld")).texture("layer0", modLoc("item/" + name));
  }

  private void blockItem(String name) {
    withExistingParent(name, modLoc("block/" + name));
  }

  private void blockItem(String itemName, String blockModelName) {
    withExistingParent(itemName, modLoc("block/" + blockModelName));
  }
}
