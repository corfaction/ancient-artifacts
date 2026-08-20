package net.corfaction.ancientartifacts.client.entity.guardian_ghost;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
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

public final class GuardianGhostModel extends EntityModel<LivingEntityRenderState> {

	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(AncientArtifacts.id("guardian_ghost_model"), "main");

	private final ModelPart body;

	public GuardianGhostModel(ModelPart root) {
		super(root);
		this.body = root.getChild(PartNames.BODY);
	}

	public static void registerModelLayers() {
		ModelLayerRegistry.registerModelLayer(
				LAYER_LOCATION,
				GuardianGhostModel::createBodyLayer
		);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition root = meshDefinition.getRoot();

		root.addOrReplaceChild(
				PartNames.BODY,
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(
								-2.0F,
								-8.0F,
								-2.0F,
								4.0F,
								4.0F,
								4.0F,
								new CubeDeformation(0.0F)
						)
						.texOffs(0, 8)
						.addBox(
								-1.0F,
								-4.0F,
								-1.0F,
								2.0F,
								2.0F,
								2.0F,
								new CubeDeformation(0.0F)
						)
						.texOffs(8, 8)
						.addBox(
								-1.0F,
								-3.0F,
								-2.0F,
								2.0F,
								2.0F,
								1.0F,
								new CubeDeformation(0.0F)
						)
						.texOffs(8, 11)
						.addBox(
								-2.0F,
								-4.0F,
								-1.0F,
								1.0F,
								2.0F,
								1.0F,
								new CubeDeformation(0.0F)
						)
						.texOffs(0, 12)
						.addBox(
								1.0F,
								-4.0F,
								-1.0F,
								1.0F,
								2.0F,
								1.0F,
								new CubeDeformation(0.0F)
						),
				PartPose.offset(0.0F, 24.0F, 0.0F)
		);

		return LayerDefinition.create(meshDefinition, 16, 16);
	}
}