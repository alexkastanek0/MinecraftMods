package com.example.simpleautofarm.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;

import com.example.simpleautofarm.registry.ModBlockEntities;

import com.example.simpleautofarm.menu.HarvesterMenu;

public class HarvesterBlockEntity extends MachineInventoryBlockEntity {
    public HarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARVESTER, pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.simpleautofarm.harvester");
    }

    @Override
    public AbstractContainerMenu createMenu(
        int containerId,
        Inventory inventory,
        Player player
    ) {
        return new HarvesterMenu(containerId, inventory, this);
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
