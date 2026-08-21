package net.corfaction.ancientartifacts.artifact;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ArchaeologicalRecipe(
        Item input,
        Item output,
        Identifier texture,
        Identifier outputTexture
) {

    public boolean matches(ItemStack stack) {
        return stack.is(input);
    }
}