package net.corfaction.ancientartifacts.entity;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ModEntityTypes {

    public static final EntityType<Djinn> DJINN = register(
            "djinn",
            EntityType.Builder.of(Djinn::new, MobCategory.CREATURE)
                    .sized(0.5F, 0.2F)
    );

    public static final EntityType<AncientGhost> ANCIENT_GHOST = register(
            "ancient_ghost",
            EntityType.Builder.of(AncientGhost::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.9F)
    );

    private static <T extends Entity> EntityType<T> register(
            String name,
            EntityType.Builder<T> builder
    ) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(
                Registries.ENTITY_TYPE,
                AncientArtifacts.id(name)
        );

        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                key,
                builder.build(key)
        );
    }

    public static void registerModEntityTypes() {
        AncientArtifacts.LOGGER.info(
                "Registering EntityTypes for {}",
                AncientArtifacts.MOD_ID
        );
    }

    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(
                DJINN,
                Djinn.createAttributes()
        );

        FabricDefaultAttributeRegistry.register(
                ANCIENT_GHOST,
                AncientGhost.createAttributes()
        );
    }

    public static void registerSpawnPlacements() {
        SpawnPlacements.register(
                DJINN,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkSurfaceMonstersSpawnRules
        );

        SpawnPlacements.register(
                ANCIENT_GHOST,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkSurfaceMonstersSpawnRules
        );
    }
}