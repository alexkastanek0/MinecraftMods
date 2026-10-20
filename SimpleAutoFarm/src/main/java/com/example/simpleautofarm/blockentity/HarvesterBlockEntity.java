package com.example.simpleautofarm.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;

import com.example.simpleautofarm.registry.ModBlockEntities;

public class HarvesterBlockEntity extends MachineInventoryBlockEntity {
    public HarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARVESTER, pos, state);
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return stack.is(Items.WHEAT)
            || stack.is(Items.WHEAT_SEEDS)
            || stack.is(Items.CARROT)
            || stack.is(Items.POTATO)
            || stack.is(Items.BEETROOT)
            || stack.is(Items.BEETROOT_SEEDS);
    }

    @Override
    protected boolean canAcceptFromSide(Direction side) {
        return false;
    }

    @Override
    protected boolean canExtractFromSide(Direction side) {
        return side == Direction.DOWN;
    }
}
