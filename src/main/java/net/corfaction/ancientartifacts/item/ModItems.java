package net.corfaction.ancientartifacts.item;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.corfaction.ancientartifacts.entity.ModEntityTypes;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.function.Function;

public final class ModItems {

    public static final Item MYSTERIOUS_LAMP = registerItem(
            ModItemIds.MYSTERIOUS_LAMP,
            MysteriousLamp::new,
            new Item.Properties()
    );

    public static final Item PETRIFIED_TALISMAN = registerItem(
            ModItemIds.PETRIFIED_TALISMAN,
            Item::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Item GUARDIAN_TALISMAN = registerItem(
            ModItemIds.GUARDIAN_TALISMAN,
            GuardianTalisman::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Item DIRTY_FRAGMENT = registerItem(
            ModItemIds.DIRTY_FRAGMENT,
            Item::new,
            new Item.Properties()
    );

    public static final Item ANCIENT_FRAGMENT = registerItem(
            ModItemIds.ANCIENT_FRAGMENT,
            Item::new,
            new Item.Properties()
    );

    public static final Item RUSTY_METAL_FRAGMENT = registerItem(
            ModItemIds.RUSTY_METAL_FRAGMENT,
            Item::new,
            new Item.Properties()
    );

    public static final Item METAL_FRAGMENT = registerItem(
            ModItemIds.METAL_FRAGMENT,
            Item::new,
            new Item.Properties()
    );

    public static final Item ANCIENT_GHOST_SPAWN_EGG = registerItem(
            ModItemIds.ANCIENT_GHOST_SPAWN_EGG,
            SpawnEggItem::new,
            new Item.Properties().spawnEgg(ModEntityTypes.ANCIENT_GHOST)
    );

    public static final Item PHANTOM_GRIP = registerItem(
            ModItemIds.PHANTOM_GRIP,
            Item::new,
            new Item.Properties()
    );

    public static final Item ECHO_BLADE = registerItem(
            ModItemIds.ECHO_BLADE,
            Item::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Item ARCHAEOLOGIST_EYE = registerItem(
            ModItemIds.ARCHAEOLOGIST_EYE,
            ArchaeologistEye::new,
            new Item.Properties().stacksTo(1)
    );

    private ModItems() {
    }

    public static Item registerItem(
            ResourceKey<Item> itemKey,
            Function<Item.Properties, Item> itemFactory,
            Item.Properties properties
    ) {
        Item item = itemFactory.apply(properties.setId(itemKey));

        Registry.register(
                BuiltInRegistries.ITEM,
                itemKey,
                item
        );

        return item;
    }

    public static void registerCreativeTabItems() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS)
                .register(creativeTab -> creativeTab.accept(MYSTERIOUS_LAMP));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register(creativeTab -> creativeTab.accept(PETRIFIED_TALISMAN));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register(creativeTab -> creativeTab.accept(GUARDIAN_TALISMAN));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register(creativeTab -> creativeTab.accept(DIRTY_FRAGMENT));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register(creativeTab -> creativeTab.accept(ANCIENT_FRAGMENT));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register(creativeTab -> creativeTab.accept(RUSTY_METAL_FRAGMENT));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register(creativeTab -> creativeTab.accept(METAL_FRAGMENT));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS)
                .register(creativeTab -> creativeTab.accept(ANCIENT_GHOST_SPAWN_EGG));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register(creativeTab -> creativeTab.accept(PHANTOM_GRIP));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register(creativeTab -> creativeTab.accept(ECHO_BLADE));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(creativeTab -> creativeTab.accept(ARCHAEOLOGIST_EYE));
    }
}