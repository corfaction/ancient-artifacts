package net.corfaction.ancientartifacts;

import net.corfaction.ancientartifacts.entity.ModEntity;
import net.corfaction.ancientartifacts.item.ModItems;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AncientArtifacts implements ModInitializer {
	public static final String MOD_ID = "ancient-artifacts";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerCreativeTabItems();
		ModEntity.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
