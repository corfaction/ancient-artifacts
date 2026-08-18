package net.corfaction.ancientartifacts.item;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {
    public static final ResourceKey<Item> MYSTERIOUS_LAMP = create("mysterious_lamp");

    public static ResourceKey<Item> create(String name) {
        return ResourceKey.create(Registries.ITEM, AncientArtifacts.id(name));
    }
}
