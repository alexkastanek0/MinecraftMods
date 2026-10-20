package com.example.simpleautofarm.registry;

import com.example.simpleautofarm.PlanterHarvesterSimpleAutoFarm;
import com.example.simpleautofarm.blockentity.HarvesterBlockEntity;
import com.example.simpleautofarm.blockentity.PlanterBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
    public static final BlockEntityType<PlanterBlockEntity> PLANTER =
        Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            PlanterHarvesterSimpleAutoFarm.id("planter"),
            FabricBlockEntityTypeBuilder.create(
                PlanterBlockEntity::new,
                ModBlocks.PLANTER
            ).build()
        );

    public static final BlockEntityType<HarvesterBlockEntity> HARVESTER =
        Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            PlanterHarvesterSimpleAutoFarm.id("harvester"),
            FabricBlockEntityTypeBuilder.create(
                HarvesterBlockEntity::new,
                ModBlocks.HARVESTER
            ).build()
        );

    private ModBlockEntities() {
    }

    public static void initialize() {
        // Accessing this method ensures the registered types are initialized.
    }
}
