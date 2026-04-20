package net.astralya.kindlingage.neoforge.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModAdvancementProvider extends AdvancementProvider {
  public ModAdvancementProvider(
      PackOutput output,
      CompletableFuture<HolderLookup.Provider> registries,
      ExistingFileHelper existingFileHelper) {
    super(
        output,
        registries,
        existingFileHelper,
        List.of(
            (holderLookup, writer, fileHelper) ->
                new KindlingAgeAdvancements().generate(holderLookup, writer)));
  }

  private static final class KindlingAgeAdvancements implements AdvancementSubProvider {
    private static final ResourceLocation BACKGROUND =
        ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/adventure.png");

    @Override
    public void generate(
        HolderLookup.Provider registries, java.util.function.Consumer<AdvancementHolder> writer) {
      AdvancementHolder root =
          advancement()
              .display(
                  ModItems.FLINT_FLAKE.get(),
                  title("root"),
                  description("root"),
                  BACKGROUND,
                  AdvancementType.TASK,
                  true,
                  true,
                  false)
              .addCriterion("has_flint", hasItems(Items.FLINT))
              .save(writer, id("root"));

      AdvancementHolder spark =
          advancement()
              .parent(root)
              .display(
                  ModItems.KINDLING.get(),
                  title("spark"),
                  description("spark"),
                  null,
                  AdvancementType.TASK,
                  true,
                  true,
                  false)
              .addCriterion("has_kindling", hasItems(ModItems.KINDLING.get()))
              .rewards(
                  AdvancementRewards.Builder.recipe(
                      ResourceLocation.fromNamespaceAndPath(KindlingAge.MOD_ID, "hearth_pit")))
              .save(writer, id("spark"));

      AdvancementHolder goneFishing =
          advancement()
              .parent(root)
              .display(
                  ModItems.FISH_TRAP.get(),
                  title("gone_fishing"),
                  description("gone_fishing"),
                  null,
                  AdvancementType.TASK,
                  true,
                  true,
                  false)
              .addCriterion("has_fish_trap", hasItems(ModItems.FISH_TRAP.get()))
              .save(writer, id("gone_fishing"));

      AdvancementHolder sleepingWithTheFishes =
          advancement()
              .parent(goneFishing)
              .display(
                  ModItems.WILD_SEEDS.get(),
                  title("sleeping_with_the_fishes"),
                  description("sleeping_with_the_fishes"),
                  null,
                  AdvancementType.GOAL,
                  true,
                  true,
                  false)
              .addCriterion(
                  "bait_fish_trap",
                  ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                      net.minecraft.advancements.critereon.LocationPredicate.Builder.location()
                          .setBlock(BlockPredicate.Builder.block().of(ModBlocks.FISH_TRAP.get())),
                      ItemPredicate.Builder.item().of(ModItems.WILD_SEEDS.get())))
              .save(writer, id("sleeping_with_the_fishes"));

      AdvancementHolder offTheRack =
          advancement()
              .parent(root)
              .display(
                  ModItems.DRYING_RACK.get(),
                  title("off_the_rack"),
                  description("off_the_rack"),
                  null,
                  AdvancementType.TASK,
                  true,
                  true,
                  false)
              .addCriterion("has_drying_rack", hasItems(ModItems.DRYING_RACK.get()))
              .save(writer, id("off_the_rack"));

      AdvancementHolder somethingInTheAir =
          advancement()
              .parent(offTheRack)
              .display(
                  ModItems.DRIED_FISH.get(),
                  title("something_in_the_air"),
                  description("something_in_the_air"),
                  null,
                  AdvancementType.GOAL,
                  true,
                  true,
                  false)
              .addCriterion("has_dried_fish", hasItems(ModItems.DRIED_FISH.get()))
              .addCriterion("has_jerky", hasItems(ModItems.JERKY.get()))
              .requirements(
                  AdvancementRequirements.anyOf(List.of("has_dried_fish", "has_jerky")))
              .save(writer, id("something_in_the_air"));

      AdvancementHolder leatherWeather =
          advancement()
              .parent(offTheRack)
              .display(
                  Items.LEATHER,
                  title("leather_weather"),
                  description("leather_weather"),
                  null,
                  AdvancementType.TASK,
                  true,
                  true,
                  false)
              .addCriterion("has_leather", hasItems(Items.LEATHER))
              .save(writer, id("leather_weather"));

      AdvancementHolder potLuck =
          advancement()
              .parent(root)
              .display(
                  ModItems.CLAY_POT.get(),
                  title("pot_luck"),
                  description("pot_luck"),
                  null,
                  AdvancementType.TASK,
                  true,
                  true,
                  false)
              .addCriterion("has_wet_clay_pot", hasItems(ModItems.WET_CLAY_POT.get()))
              .addCriterion("has_clay_pot", hasItems(ModItems.CLAY_POT.get()))
              .requirements(
                  AdvancementRequirements.allOf(List.of("has_wet_clay_pot", "has_clay_pot")))
              .save(writer, id("pot_luck"));

      advancement()
          .parent(potLuck)
          .display(
              ModItems.WATER_REED_SCOOP.get(),
              title("water_you_waiting_for"),
              description("water_you_waiting_for"),
              null,
              AdvancementType.TASK,
              true,
              true,
              false)
          .addCriterion("has_water_reed_scoop", hasItems(ModItems.WATER_REED_SCOOP.get()))
          .save(writer, id("water_you_waiting_for"));

      advancement()
          .parent(spark)
          .display(
              ModBlocks.HEARTH_PIT.get(),
              title("where_theres_smoke"),
              description("where_theres_smoke"),
              null,
              AdvancementType.TASK,
              true,
              true,
              false)
          .addCriterion("has_hearth_pit", hasItems(ModBlocks.HEARTH_PIT.get()))
          .save(writer, id("where_theres_smoke"));

      advancement()
          .parent(root)
          .display(
              ModItems.HUNTING_SPEAR.get(),
              title("point_taken"),
              description("point_taken"),
              null,
              AdvancementType.TASK,
              true,
              true,
              false)
          .addCriterion("has_hunting_spear", hasItems(ModItems.HUNTING_SPEAR.get()))
          .save(writer, id("point_taken"));

      advancement()
          .parent(root)
          .display(
              ModItems.FIBER_CORD.get(),
              title("the_long_and_short_of_it"),
              description("the_long_and_short_of_it"),
              null,
              AdvancementType.TASK,
              true,
              true,
              false)
          .addCriterion("has_fiber_cord", hasItems(ModItems.FIBER_CORD.get()))
          .save(writer, id("the_long_and_short_of_it"));

      advancement()
          .parent(root)
          .display(
              ModItems.ROASTED_SEEDS.get(),
              title("seed_you_later"),
              description("seed_you_later"),
              null,
              AdvancementType.TASK,
              true,
              true,
              false)
          .addCriterion("has_roasted_seeds", hasItems(ModItems.ROASTED_SEEDS.get()))
          .save(writer, id("seed_you_later"));
    }

    private static Advancement.Builder advancement() {
      return Advancement.Builder.advancement();
    }

    private static net.minecraft.advancements.Criterion<?> hasItems(
        net.minecraft.world.level.ItemLike... items) {
      return InventoryChangeTrigger.TriggerInstance.hasItems(items);
    }

    private static Component title(String path) {
      return Component.translatable("advancements." + KindlingAge.MOD_ID + "." + path + ".title");
    }

    private static Component description(String path) {
      return Component.translatable(
          "advancements." + KindlingAge.MOD_ID + "." + path + ".description");
    }

    private static String id(String path) {
      return KindlingAge.MOD_ID + ":" + path;
    }
  }
}
