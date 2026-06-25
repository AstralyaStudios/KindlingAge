package net.astralya.kindlingage.forge.datagen;

import net.astralya.kindlingage.KindlingAge;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class ModBlockStateProvider extends BlockStateProvider {
  public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, KindlingAge.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {}
}
