package net.corfaction.ancientartifacts.block.menu;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {

    public static final MenuType<ArchaeologicalTableMenu> ARCHAEOLOGICAL_TABLE =
            register(
                    "archaeological_table",
                    new MenuType<>(
                            ArchaeologicalTableMenu::new,
                            null
                    )
            );

    private static <T extends MenuType<?>> T register(
            String name,
            T menuType
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(AncientArtifacts.MOD_ID, name);

        ResourceKey<MenuType<?>> key = ResourceKey.create(Registries.MENU, id);

        return Registry.register(BuiltInRegistries.MENU, key, menuType);
    }

    public static void initialize() {
        AncientArtifacts.LOGGER.info("Registering menu types for {}", AncientArtifacts.MOD_ID);
    }
}