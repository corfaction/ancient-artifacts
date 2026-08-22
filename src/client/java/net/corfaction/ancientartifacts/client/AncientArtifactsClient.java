package net.corfaction.ancientartifacts.client;

import net.corfaction.ancientartifacts.block.menu.ModMenuTypes;
import net.corfaction.ancientartifacts.client.entity.ClientPhantomSweep;
import net.corfaction.ancientartifacts.client.entity.ModEntityModelLayers;
import net.corfaction.ancientartifacts.client.entity.ancient_ghost.AncientGhostRenderer;
import net.corfaction.ancientartifacts.client.entity.djinn.DjinnRenderer;
import net.corfaction.ancientartifacts.client.gui.ArchaeologicalTableScreen;
import net.corfaction.ancientartifacts.entity.ModEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;

public final class AncientArtifactsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityRenderers.register(ModEntityTypes.DJINN, DjinnRenderer::new);
		EntityRenderers.register(ModEntityTypes.ANCIENT_GHOST, AncientGhostRenderer::new);

		MenuScreens.register(
				ModMenuTypes.ARCHAEOLOGICAL_TABLE,
				ArchaeologicalTableScreen::new
		);

		ClientPhantomSweep.register();

		ModEntityModelLayers.registerModelLayers();
	}
}