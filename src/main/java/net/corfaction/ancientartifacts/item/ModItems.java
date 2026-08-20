package net.corfaction.ancientartifacts.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

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

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(creativeTab -> creativeTab.accept(GUARDIAN_TALISMAN));
    }
}