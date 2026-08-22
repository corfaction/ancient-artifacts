package net.corfaction.ancientartifacts.client.entity;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.corfaction.ancientartifacts.client.entity.ancient_ghost.AncientGhostModel;
import net.corfaction.ancientartifacts.client.entity.guardian_ghost.GuardianGhostModel;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;

public final class ModEntityModelLayers {

    public static final ModelLayerLocation ANCIENT_GHOST = createMain("ancient_ghost");
    public static final ModelLayerLocation GUARDIAN_GHOST = createMain("guardian_ghost");

    private static ModelLayerLocation createMain(String name) {
        return new ModelLayerLocation(AncientArtifacts.id(name), "main");
    }

    public static void registerModelLayers() {
        ModelLayerRegistry.registerModelLayer(
                ANCIENT_GHOST,
                AncientGhostModel::getTexturedModelData
        );
        ModelLayerRegistry.registerModelLayer(
                GUARDIAN_GHOST,
                GuardianGhostModel::getTexturedModelData
        );
    }
}