package net.corfaction.ancientartifacts.item;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {

    public static final TagKey<Item> ARTIFACT = TagKey.create(
            Registries.ITEM, AncientArtifacts.id("artifact")
    );

}