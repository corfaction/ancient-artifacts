package net.corfaction.ancientartifacts.mixin.activated_artifact;

import net.corfaction.ancientartifacts.component.ModDataComponents;
import net.corfaction.ancientartifacts.item.ModItemTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(
            method = "addDetailsToTooltip",
            at = @At("TAIL")
    )
    private void ancientArtifacts$addActivationTooltip(
            Item.TooltipContext context,
            TooltipDisplay display,
            Player player,
            TooltipFlag tooltipFlag,
            Consumer<Component> builder,
            CallbackInfo ci
    ) {
        ItemStack stack = (ItemStack) (Object) this;

        if (!stack.is(ModItemTags.ARTIFACT)) {
            return;
        }

        boolean activated = stack.getOrDefault(
                ModDataComponents.ACTIVATED,
                false
        );

        builder.accept(
                Component.literal("Activated: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(
                                Component.literal(String.valueOf(activated))
                                        .withStyle(
                                                activated
                                                        ? ChatFormatting.GREEN
                                                        : ChatFormatting.RED
                                        )
                        )
        );
    }
}