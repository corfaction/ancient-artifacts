package net.corfaction.ancientartifacts.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {

    public static final Item MYSTERIOUS_LAMP = registerItem(
            ModItemIds.MYSTERIOUS_LAMP,
            MysteriousLamp::new,
            new Item.Properties()
    );

    public static Item registerItem(ResourceKey<Item> itemKey, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        Item item = itemFactory.apply(settings.setId(itemKey));

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void registerCreativeTabItems() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(creativeTab -> {
            creativeTab.accept(ModItems.MYSTERIOUS_LAMP);
        });
    }
}
