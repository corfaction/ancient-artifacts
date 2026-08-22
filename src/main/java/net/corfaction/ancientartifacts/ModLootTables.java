package net.corfaction.ancientartifacts;

import net.corfaction.ancientartifacts.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public final class ModLootTables {

    private static final ResourceKey<LootTable> DESERT_PYRAMID_ARCHAEOLOGY =
            archaeologyTable("desert_pyramid");
    private static final ResourceKey<LootTable> DESERT_WELL_ARCHAEOLOGY =
            archaeologyTable("desert_well");
    private static final ResourceKey<LootTable> OCEAN_RUIN_COLD_ARCHAEOLOGY =
            archaeologyTable("ocean_ruin_cold");
    private static final ResourceKey<LootTable> OCEAN_RUIN_WARM_ARCHAEOLOGY =
            archaeologyTable("ocean_ruin_warm");
    private static final ResourceKey<LootTable> TRAIL_RUINS_COMMON_ARCHAEOLOGY =
            archaeologyTable("trail_ruins_common");
    private static final ResourceKey<LootTable> TRAIL_RUINS_RARE_ARCHAEOLOGY =
            archaeologyTable("trail_ruins_rare");

    private static final Set<ResourceKey<LootTable>> ARCHAEOLOGY_TABLES = Set.of(
            DESERT_PYRAMID_ARCHAEOLOGY,
            DESERT_WELL_ARCHAEOLOGY,
            OCEAN_RUIN_COLD_ARCHAEOLOGY,
            OCEAN_RUIN_WARM_ARCHAEOLOGY,
            TRAIL_RUINS_COMMON_ARCHAEOLOGY,
            TRAIL_RUINS_RARE_ARCHAEOLOGY
    );

    private ModLootTables() {
    }

    private static ResourceKey<LootTable> archaeologyTable(String name) {
        return ResourceKey.create(
                Registries.LOOT_TABLE,
                Identifier.parse("minecraft:archaeology/" + name)
        );
    }

    public static void register() {
        LootTableEvents.MODIFY.register(
                (key, tableBuilder, source, registries) -> {
                    if (!source.isBuiltin()) {
                        return;
                    }

                    if (key.equals(BuiltInLootTables.DESERT_PYRAMID)
                            || key.equals(BuiltInLootTables.JUNGLE_TEMPLE)) {
                        addMysteriousLamp(tableBuilder);
                    }

                    if (ARCHAEOLOGY_TABLES.contains(key)) {
                        addArchaeologicalItems(tableBuilder);
                    }
                }
        );
    }

    private static void addMysteriousLamp(LootTable.Builder tableBuilder) {
        tableBuilder.pool(
                LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.MYSTERIOUS_LAMP).setWeight(1))
                        .add(EmptyLootItem.emptyItem().setWeight(3))
                        .build()
        );
    }

    private static void addArchaeologicalItems(LootTable.Builder tableBuilder) {
        tableBuilder.modifyPools(pool ->
                pool.add(
                        LootItem.lootTableItem(ModItems.PETRIFIED_TALISMAN)
                                .setWeight(9)
                )
        );

        tableBuilder.modifyPools(pool ->
                pool.add(
                        LootItem.lootTableItem(ModItems.DIRTY_FRAGMENT)
                                .setWeight(15)
                )
        );

        tableBuilder.modifyPools(pool ->
                pool.add(
                        LootItem.lootTableItem(ModItems.RUSTY_METAL_FRAGMENT)
                                .setWeight(15)
                )
        );
    }
}