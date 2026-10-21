package com.example.simpleautofarm.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;

import com.example.simpleautofarm.registry.ModBlockEntities;
import com.example.simpleautofarm.farming.CropType;
import com.example.simpleautofarm.menu.PlanterMenu;

public class PlanterBlockEntity extends MachineInventoryBlockEntity {
    public PlanterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLANTER, pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.simpleautofarm.planter");
    }

    @Override
    public AbstractContainerMenu createMenu(
        int containerId,
        Inventory inventory,
        Player player
    ) {
        return new PlanterMenu(containerId, inventory, this);
    }

    /**
     * Returns the crop position directly in front of the planter.
     */
    public BlockPos getTargetCropPos() {
        Direction facing = getBlockState().getValue(
            BlockStateProperties.HORIZONTAL_FACING
        );

        return getBlockPos().relative(facing);
    }

    /**
     * Returns the farmland position directly below the crop position.
     */
    public BlockPos getTargetFarmlandPos() {
        return getTargetCropPos().below();
    }

    /**
     * Checks whether the crop position is empty and farmland
     * exists immediately beneath it.
     */
    public boolean hasValidPlantingTarget() {
        Level level = getLevel();

        if (level == null) {
            return false;
        }

        BlockPos cropPos = getTargetCropPos();
        BlockPos farmlandPos = cropPos.below();

        boolean isCropSpaceEmpty = level.isEmptyBlock(cropPos);
        boolean isFarmland = level.getBlockState(farmlandPos)
            .is(Blocks.FARMLAND);

        return isCropSpaceEmpty && isFarmland;
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return CropType.isValidPlantingItem(stack);
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
