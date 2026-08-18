package net.corfaction.ancientartifacts;

import net.corfaction.ancientartifacts.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ModLootTables {

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            if (!source.isBuiltin()) {
                return;
            }

            if (key.equals(BuiltInLootTables.DESERT_PYRAMID) || key.equals(BuiltInLootTables.JUNGLE_TEMPLE)) {
                addMysteriousLamp(tableBuilder);
            }
        });
    }

    private static void addMysteriousLamp(
            LootTable.Builder tableBuilder
    ) {
        tableBuilder.pool(
                LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(
                                LootItem.lootTableItem(ModItems.MYSTERIOUS_LAMP)
                                        .setWeight(1)
                        )
                        .add(
                                EmptyLootItem.emptyItem()
                                        .setWeight(3)
                        )
                        .build()
        );
    }
}