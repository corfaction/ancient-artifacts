package net.corfaction.ancientartifacts.client.item;

import net.corfaction.ancientartifacts.block.ModBlocks;
import net.corfaction.ancientartifacts.item.ModItems;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;

public class ModItemDescription {
    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.is(ModItems.ARCHAEOLOGIST_EYE)) {
                lines.add(Component.translatable(
                        "item.ancient-artifacts.archaeologist_eye.desc"
                ).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable(
                        "item.ancient-artifacts.archaeologist_eye.desc.1"
                ).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable(
                        "item.ancient-artifacts.archaeologist_eye.desc.2"
                ));
            }
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.is(ModItems.ECHO_BLADE)) {
                lines.add(Component.translatable(
                        "item.ancient-artifacts.echo_blade.desc"
                ).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable(
                        "item.ancient-artifacts.echo_blade.desc.1"
                ).withStyle(ChatFormatting.GRAY));
            }
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.is(ModItems.GUARDIAN_TALISMAN)) {
                lines.add(Component.translatable(
                        "item.ancient-artifacts.guardian_talisman.desc"
                ).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable(
                        "item.ancient-artifacts.guardian_talisman.desc.1"
                ).withStyle(ChatFormatting.GRAY));
            }
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == ModBlocks.ACTIVATION_ALTAR) {
                lines.add(Component.translatable(
                        "block.ancient-artifacts.activation_altar.desc"
                ).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable(
                        "block.ancient-artifacts.activation_altar.desc.1"
                ).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable(
                        "block.ancient-artifacts.activation_altar.desc.2"
                ).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable(
                        "block.ancient-artifacts.activation_altar.desc.3"
                ).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable(
                        "block.ancient-artifacts.activation_altar.desc.4"
                ).withStyle(ChatFormatting.GRAY));
            }
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == ModBlocks.ARCHAEOLOGICAL_TABLE) {
                lines.add(Component.translatable(
                        "block.ancient-artifacts.archaeological_table.desc"
                ).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable(
                        "block.ancient-artifacts.archaeological_table.desc.1"
                ).withStyle(ChatFormatting.GRAY));
            }
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.is(ModItems.MYSTERIOUS_LAMP)) {
                lines.add(Component.translatable(
                        "item.ancient-artifacts.mysterious_lamp.desc"
                ).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable(
                        "item.ancient-artifacts.mysterious_lamp.desc.1"
                ).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable(
                        "item.ancient-artifacts.mysterious_lamp.desc.2"
                ));
            }
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.is(ModItems.GHOST_SOUL)) {
                lines.add(Component.translatable(
                        "item.ancient-artifacts.ghost_soul.desc"
                ).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable(
                        "item.ancient-artifacts.ghost_soul.desc.1"
                ));
            }
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.is(ModItems.SKYBOUND_TALISMAN)) {
                lines.add(Component.translatable(
                        "item.ancient-artifacts.skybound_talisman.desc"
                ).withStyle(ChatFormatting.GRAY));
            }
        });
    }
}
