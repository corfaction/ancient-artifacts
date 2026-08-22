package net.corfaction.ancientartifacts.client.entity.ancient_ghost;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public final class AncientGhostModel extends EntityModel<LivingEntityRenderState> {

    public AncientGhostModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        root.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create()
                        .texOffs(0, 0)

                        .addBox(-2.0F, -12.0F, -2.0F, 4.0F, 7.0F, 4.0F,
                                new CubeDeformation(0.0F)).texOffs(0, 12)

                        .addBox(-1.0F, -11.0F, -1.0F, 2.0F, 8.0F, 2.0F,
                                new CubeDeformation(0.0F)).texOffs(0, 21)

                        .addBox(-3.0F, -14.0F, -3.0F, 6.0F, 5.0F, 6.0F,
                                new CubeDeformation(0.0F)),

                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        return LayerDefinition.create(meshDefinition, 32, 32);
    }
}