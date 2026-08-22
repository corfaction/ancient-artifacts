package net.corfaction.ancientartifacts.item;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class ModItemIds {

    public static final ResourceKey<Item> MYSTERIOUS_LAMP =
            create("mysterious_lamp");

    public static final ResourceKey<Item> PETRIFIED_TALISMAN =
            create("petrified_talisman");

    public static final ResourceKey<Item> GUARDIAN_TALISMAN =
            create("guardian_talisman");

    public static final ResourceKey<Item> DIRTY_FRAGMENT =
            create("dirty_fragment");

    public static final ResourceKey<Item> ANCIENT_FRAGMENT =
            create("ancient_fragment");

    public static final ResourceKey<Item> RUSTY_METAL_FRAGMENT =
            create("rusty_metal_fragment");

    public static final ResourceKey<Item> METAL_FRAGMENT =
            create("metal_fragment");

    public static final ResourceKey<Item> ANCIENT_GHOST_SPAWN_EGG =
            create("ancient_ghost_spawn_egg");

    public static final ResourceKey<Item> PHANTOM_GRIP =
            create("phantom_grip");

    public static final ResourceKey<Item> ECHO_BLADE =
            create("echo_blade");

    public static ResourceKey<Item> create(String name) {
        return ResourceKey.create(
                Registries.ITEM,
                AncientArtifacts.id(name)
        );
    }
}