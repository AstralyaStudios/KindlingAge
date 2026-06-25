package net.astralya.kindlingage;

import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.block.entity.ModBlockEntityTypes;
import net.astralya.kindlingage.entity.ModEntityTypes;
import net.astralya.kindlingage.item.ModCreativeModeTabs;
import net.astralya.kindlingage.item.ModItems;
import net.astralya.kindlingage.recipe.ModRecipeTypes;
import net.astralya.kindlingage.screen.ModMenuTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KindlingAge {
  public static final String MOD_ID = "kindlingage";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  private KindlingAge() {}

  public static void init() {
    ModEntityTypes.init();
    ModBlocks.init();
    ModBlockEntityTypes.init();
    ModItems.init();
    ModRecipeTypes.init();
    ModMenuTypes.init();
    ModCreativeModeTabs.init();
  }
}
