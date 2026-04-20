package net.astralya.kindlingage.util;

import net.astralya.kindlingage.KindlingAge;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("SameParameterValue")
public final class ModTags {
  private ModTags() {}

  public static final class Blocks {
    public static final TagKey<Block> FLINT_KNIFE_FIBER_SOURCES =
        create("flint_knife_fiber_sources");
    public static final TagKey<Block> FLINT_KNIFE_WILD_SEED_SOURCES =
        create("flint_knife_wild_seed_sources");
    public static final TagKey<Block> FLINT_KNIFE_STICK_SOURCES =
        create("flint_knife_stick_sources");

    private Blocks() {}

    private static TagKey<Block> create(String name) {
      return TagKey.create(
          Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(KindlingAge.MOD_ID, name));
    }
  }

  public static final class EntityTypes {
    public static final TagKey<EntityType<?>> SMALL_GAME = create("small_game");

    private EntityTypes() {}

    private static TagKey<EntityType<?>> create(String name) {
      return TagKey.create(
          Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KindlingAge.MOD_ID, name));
    }
  }
}
