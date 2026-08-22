package net.corfaction.ancientartifacts.block.entity;

import java.util.Set;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.corfaction.ancientartifacts.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntityTypes {

    public static final BlockEntityType<ArchaeologicalTableBlockEntity> ARCHAEOLOGICAL_TABLE =
            register(
                    "archaeological_table",
                    new BlockEntityType<>(
                            ArchaeologicalTableBlockEntity::new,
                            Set.of(ModBlocks.ARCHAEOLOGICAL_TABLE)
                    )
            );

    private ModBlockEntityTypes() {
    }

    private static <T extends BlockEntityType<?>> T register(String name, T blockEntityType) {
        Identifier id = AncientArtifacts.id(name);
        ResourceKey<BlockEntityType<?>> key = ResourceKey.create(
                Registries.BLOCK_ENTITY_TYPE,
                id
        );

        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, blockEntityType);
    }

    public static void initialize() {
        AncientArtifacts.LOGGER.info(
                "Registering block entities for {}",
                AncientArtifacts.MOD_ID
        );
    }
}