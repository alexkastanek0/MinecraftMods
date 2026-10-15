package com.example.simpleautofarm.registry;

import java.util.function.Function;

import com.example.simpleautofarm.PlanterHarvesterSimpleAutoFarm;
import com.example.simpleautofarm.block.MachineBlock;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {

    public static final Block PLANTER = register(
        blockItemId("planter"),
        MachineBlock::new,
        BlockBehaviour.Properties.of()
            .strength(3.5F)
            .sound(SoundType.STONE)
    );

    public static final Block HARVESTER = register(
        blockItemId("harvester"),
        MachineBlock::new,
        BlockBehaviour.Properties.of()
            .strength(3.5F)
            .sound(SoundType.STONE)
    );

    private ModBlocks() {
    }

    private static BlockItemId blockItemId(String path) {
        Identifier id = PlanterHarvesterSimpleAutoFarm.id(path);
        return BlockItemId.create(id, id);
    }

    private static Block register(
        BlockItemId id,
        Function<BlockBehaviour.Properties, Block> blockFactory,
        BlockBehaviour.Properties properties
    ) {
        Block block = Registry.register(
            BuiltInRegistries.BLOCK,
            id.block(),
            blockFactory.apply(properties.setId(id.block()))
        );

        BlockItem item = new BlockItem(
            block,
            new Item.Properties()
                .useBlockDescriptionPrefix()
                .setId(id.item())
        );

        Registry.register(BuiltInRegistries.ITEM, id.item(), item);

        return block;
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(
            CreativeModeTabs.BUILDING_BLOCKS
        ).register(tab -> {
            tab.accept(PLANTER.asItem());
            tab.accept(HARVESTER.asItem());
        });
    }
    
}
