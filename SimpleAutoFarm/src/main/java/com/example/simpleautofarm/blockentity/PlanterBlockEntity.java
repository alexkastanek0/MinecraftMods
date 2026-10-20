package com.example.simpleautofarm.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;

import com.example.simpleautofarm.registry.ModBlockEntities;

public class PlanterBlockEntity extends MachineInventoryBlockEntity {
    public PlanterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLANTER, pos, state);
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return stack.is(Items.WHEAT_SEEDS)
            || stack.is(Items.CARROT)
            || stack.is(Items.POTATO)
            || stack.is(Items.BEETROOT_SEEDS);
    }

    @Override
    protected boolean canAcceptFromSide(Direction side) {
        return side != Direction.DOWN;
    }

    @Override
    protected boolean canExtractFromSide(Direction side) {
        return false;
    }
}
