package net.corfaction.ancientartifacts.artifact;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.corfaction.ancientartifacts.item.ModItems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ArchaeologicalRecipes {

    private static final List<ArchaeologicalRecipe> RECIPES = new ArrayList<>();

    private ArchaeologicalRecipes() {
    }

    public static void register(Item input, Item output, Identifier inputTexture, Identifier outputTexture) {
        RECIPES.add(new ArchaeologicalRecipe(input, output, inputTexture, outputTexture));
    }

    public static ArchaeologicalRecipe getRecipe(ItemStack stack) {
        for (ArchaeologicalRecipe recipe : RECIPES) {
            if (recipe.matches(stack)) {
                return recipe;
            }
        }

        return null;
    }

    public static boolean isValidInput(ItemStack stack) {
        return getRecipe(stack) != null;
    }

    public static void registerArchaeologicalRecipes() {
        register(
                ModItems.PETRIFIED_TALISMAN,
                ModItems.GUARDIAN_TALISMAN,
                AncientArtifacts.id("textures/item/petrified_talisman.png"),
                AncientArtifacts.id("textures/item/guardian_talisman.png")
        );
        register(
                ModItems.RUSTY_METAL_FRAGMENT,
                ModItems.METAL_FRAGMENT,
                AncientArtifacts.id("textures/item/rusty_metal_fragment.png"),
                AncientArtifacts.id("textures/item/metal_fragment.png")
        );
        register(
                ModItems.DIRTY_FRAGMENT,
                ModItems.ANCIENT_FRAGMENT,
                AncientArtifacts.id("textures/item/dirty_fragment.png"),
                AncientArtifacts.id("textures/item/ancient_fragment.png")
        );
        register(
                ModItems.PETRIFIED_TALISMAN_1,
                ModItems.SKYBOUND_TALISMAN,
                AncientArtifacts.id("textures/item/petrified_talisman_1.png"),
                AncientArtifacts.id("textures/item/skybound_talisman.png")
        );
    }
}