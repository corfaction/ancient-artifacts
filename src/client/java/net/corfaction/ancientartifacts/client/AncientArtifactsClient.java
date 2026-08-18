package net.corfaction.ancientartifacts.client;

import net.corfaction.ancientartifacts.client.entity.DjinnRenderer;
import net.corfaction.ancientartifacts.entity.ModEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class AncientArtifactsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRenderers.register(ModEntityTypes.DJINN, DjinnRenderer::new);
	}
}