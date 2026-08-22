package net.corfaction.ancientartifacts.block;

import java.util.function.Function;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {

    public static final Block ARCHAEOLOGICAL_TABLE = register(
            "archaeological_table",
            ArchaeologicalTableBlock::new,
            BlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD),
            CreativeModeTabs.FUNCTIONAL_BLOCKS
    );

    private ModBlocks() {
    }

    private static Block register(
            String name,
            Function<BlockBehaviour.Properties, Block> factory,
            BlockBehaviour.Properties properties,
            ResourceKey<CreativeModeTab> tab
    ) {
        Identifier id = AncientArtifacts.id(name);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);

        Block block = factory.apply(properties.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        BlockItem blockItem = new BlockItem(
                block,
                new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()
        );

        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);

        CreativeModeTabEvents.modifyOutputEvent(tab).register(
                creativeTab -> creativeTab.accept(blockItem)
        );

        return block;
    }

    public static void initialize() {
        AncientArtifacts.LOGGER.info(
                "Registering blocks for {}",
                AncientArtifacts.MOD_ID
        );
    }
}