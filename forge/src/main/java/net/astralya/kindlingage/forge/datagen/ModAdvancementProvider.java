package net.astralya.kindlingage.forge.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.astralya.kindlingage.KindlingAge;
import net.astralya.kindlingage.block.ModBlocks;
import net.astralya.kindlingage.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
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
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

public final class ModAdvancementProvider extends ForgeAdvancementProvider {
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
        new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png");

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> writer) {
      Advancement root =
          advancement()
              .display(
                  ModItems.FLINT_FLAKE.get(),
                  title("root"),
                  description("root"),
                  BACKGROUND,
                  FrameType.TASK,
                  true,
                  true,
                  false)
              .addCriterion("has_flint", hasItems(Items.FLINT))
              .save(writer, idString("root"));

      Advancement spark =
          advancement()
              .parent(root)
              .display(
                  ModItems.KINDLING.get(),
                  title("spark"),
                  description("spark"),
                  null,
                  FrameType.TASK,
                  true,
                  true,
                  false)
              .addCriterion("has_kindling", hasItems(ModItems.KINDLING.get()))
              .rewards(AdvancementRewards.Builder.recipe(id("hearth_pit")))
              .save(writer, idString("spark"));

      Advancement goneFishing =
          advancement()
              .parent(root)
              .display(
                  ModItems.FISH_TRAP.get(),
                  title("gone_fishing"),
                  description("gone_fishing"),
                  null,
                  FrameType.TASK,
                  true,
                  true,
                  false)
              .addCriterion("has_fish_trap", hasItems(ModItems.FISH_TRAP.get()))
              .save(writer, idString("gone_fishing"));

      advancement()
          .parent(goneFishing)
          .display(
              ModItems.WILD_SEEDS.get(),
              title("sleeping_with_the_fishes"),
              description("sleeping_with_the_fishes"),
              null,
              FrameType.GOAL,
              true,
              true,
              false)
          .addCriterion(
              "bait_fish_trap",
              ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                  net.minecraft.advancements.critereon.LocationPredicate.Builder.location()
                      .setBlock(BlockPredicate.Builder.block().of(ModBlocks.FISH_TRAP.get()).build()),
                  ItemPredicate.Builder.item().of(ModItems.WILD_SEEDS.get())))
          .save(writer, idString("sleeping_with_the_fishes"));

      Advancement offTheRack =
          advancement()
              .parent(root)
              .display(
                  ModItems.DRYING_RACK.get(),
                  title("off_the_rack"),
                  description("off_the_rack"),
                  null,
                  FrameType.TASK,
                  true,
                  true,
                  false)
              .addCriterion("has_drying_rack", hasItems(ModItems.DRYING_RACK.get()))
              .save(writer, idString("off_the_rack"));

      advancement()
          .parent(offTheRack)
          .display(
              ModItems.DRIED_FISH.get(),
              title("something_in_the_air"),
              description("something_in_the_air"),
              null,
              FrameType.GOAL,
              true,
              true,
              false)
          .addCriterion("has_dried_fish", hasItems(ModItems.DRIED_FISH.get()))
          .addCriterion("has_jerky", hasItems(ModItems.JERKY.get()))
          .requirements(RequirementsStrategy.OR)
          .save(writer, idString("something_in_the_air"));

      advancement()
          .parent(offTheRack)
          .display(
              Items.LEATHER,
              title("leather_weather"),
              description("leather_weather"),
              null,
              FrameType.TASK,
              true,
              true,
              false)
          .addCriterion("has_leather", hasItems(Items.LEATHER))
          .save(writer, idString("leather_weather"));

      Advancement potLuck =
          advancement()
              .parent(root)
              .display(
                  ModItems.CLAY_POT.get(),
                  title("pot_luck"),
                  description("pot_luck"),
                  null,
                  FrameType.TASK,
                  true,
                  true,
                  false)
              .addCriterion("has_wet_clay_pot", hasItems(ModItems.WET_CLAY_POT.get()))
              .addCriterion("has_clay_pot", hasItems(ModItems.CLAY_POT.get()))
              .save(writer, idString("pot_luck"));

      advancement()
          .parent(potLuck)
          .display(
              ModItems.WATER_REED_SCOOP.get(),
              title("water_you_waiting_for"),
              description("water_you_waiting_for"),
              null,
              FrameType.TASK,
              true,
              true,
              false)
          .addCriterion("has_water_reed_scoop", hasItems(ModItems.WATER_REED_SCOOP.get()))
          .save(writer, idString("water_you_waiting_for"));

      advancement()
          .parent(spark)
          .display(
              ModBlocks.HEARTH_PIT.get(),
              title("where_theres_smoke"),
              description("where_theres_smoke"),
              null,
              FrameType.TASK,
              true,
              true,
              false)
          .addCriterion("has_hearth_pit", hasItems(ModBlocks.HEARTH_PIT.get()))
          .save(writer, idString("where_theres_smoke"));

      advancement()
          .parent(root)
          .display(
              ModItems.HUNTING_SPEAR.get(),
              title("point_taken"),
              description("point_taken"),
              null,
              FrameType.TASK,
              true,
              true,
              false)
          .addCriterion("has_hunting_spear", hasItems(ModItems.HUNTING_SPEAR.get()))
          .save(writer, idString("point_taken"));

      advancement()
          .parent(root)
          .display(
              ModItems.FIBER_CORD.get(),
              title("the_long_and_short_of_it"),
              description("the_long_and_short_of_it"),
              null,
              FrameType.TASK,
              true,
              true,
              false)
          .addCriterion("has_fiber_cord", hasItems(ModItems.FIBER_CORD.get()))
          .save(writer, idString("the_long_and_short_of_it"));

      advancement()
          .parent(root)
          .display(
              ModItems.ROASTED_SEEDS.get(),
              title("seed_you_later"),
              description("seed_you_later"),
              null,
              FrameType.TASK,
              true,
              true,
              false)
          .addCriterion("has_roasted_seeds", hasItems(ModItems.ROASTED_SEEDS.get()))
          .save(writer, idString("seed_you_later"));
    }

    private static Advancement.Builder advancement() {
      return Advancement.Builder.advancement();
    }

    private static net.minecraft.advancements.CriterionTriggerInstance hasItems(
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

    private static ResourceLocation id(String path) {
      return new ResourceLocation(KindlingAge.MOD_ID, path);
    }

    private static String idString(String path) {
      return KindlingAge.MOD_ID + ":" + path;
    }
  }
}
