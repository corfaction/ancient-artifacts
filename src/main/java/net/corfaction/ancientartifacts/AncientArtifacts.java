package net.corfaction.ancientartifacts;

import net.corfaction.ancientartifacts.api.GuardianGhostHolder;
import net.corfaction.ancientartifacts.block.ModBlocks;
import net.corfaction.ancientartifacts.block.entity.ArchaeologicalTableBlockEntity;
import net.corfaction.ancientartifacts.block.entity.ModBlockEntityTypes;
import net.corfaction.ancientartifacts.block.menu.ArchaeologicalTableMenu;
import net.corfaction.ancientartifacts.block.menu.ModMenuTypes;
import net.corfaction.ancientartifacts.entity.ModEntity;
import net.corfaction.ancientartifacts.item.ModItems;
import net.corfaction.ancientartifacts.network.CleanArchaeologicalPixelPayload;
import net.corfaction.ancientartifacts.network.GuardianGhostStatePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AncientArtifacts implements ModInitializer {

	public static final String MOD_ID = "ancient-artifacts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerCreativeTabItems();
		ModEntity.register();
		ModLootTables.register();
		ModBlocks.initialize();
		ModBlockEntityTypes.initialize();
		ModMenuTypes.initialize();

		PayloadTypeRegistry.serverboundPlay().register(
				CleanArchaeologicalPixelPayload.TYPE,
				CleanArchaeologicalPixelPayload.CODEC
		);

		PayloadTypeRegistry.clientboundPlay().register(
				GuardianGhostStatePayload.TYPE,
				GuardianGhostStatePayload.CODEC
		);

		ServerPlayNetworking.registerGlobalReceiver(
				CleanArchaeologicalPixelPayload.TYPE,
				(payload, context) -> context.server().execute(() -> {
					if (!(context.player().containerMenu instanceof ArchaeologicalTableMenu menu)) {
						return;
					}

					if (!(menu.getContainer() instanceof ArchaeologicalTableBlockEntity table)) {
						return;
					}

					if (context.player().distanceToSqr(
							table.getBlockPos().getX() + 0.5D,
							table.getBlockPos().getY() + 0.5D,
							table.getBlockPos().getZ() + 0.5D
					) > 64.0D) {
						return;
					}

					table.cleanPixel(
							context.player(),
							payload.pixelX(),
							payload.pixelY()
					);
				})
		);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			GuardianGhostHolder ghostHolder =
					(GuardianGhostHolder) handler.getPlayer();

			ServerPlayNetworking.send(
					handler.getPlayer(),
					new GuardianGhostStatePayload(
							ghostHolder.ancientArtifacts$hasGuardianGhost()
					)
			);
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}