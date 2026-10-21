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

    /**
     * Attempts to plant one crop directly in front of the planter.
     *
     * @return true if a crop was successfully planted
     */
    public boolean tryPlant() {
        // World changes and inventory mutations must happen on the server.
        Level level = getLevel();

        if (level == null || level.isClientSide()) {
            return false;
        }

        // Check the target before examining or consuming seeds.
        if (!hasValidPlantingTarget()) {
            return false;
        }

        // Find the first valid planting item in the inventory.
        for (int slot = 0; slot < getContainerSize(); slot++) {
            ItemStack seedStack = getItem(slot);

            CropType crop = CropType.fromPlantingItem(seedStack);

            if (crop == null) {
                continue;
            }

            BlockPos cropPos = getTargetCropPos();

            // Obtain the default state of the corresponding vanilla crop.
            BlockState cropState = crop.getCropBlock().defaultBlockState();

            // Respect the crop's native survival requirements.
            if (!cropState.canSurvive(level, cropPos)) {
                return false;
            }

            // Attempt to place the crop in the world.
            if (!level.setBlock(cropPos, cropState, 3)) {
                return false;
            }

            // Consume exactly one planting item, but only after placement.
            seedStack.shrink(1);

            if (seedStack.isEmpty()) {
                items.set(slot, ItemStack.EMPTY);
            }

            setChanged();

            return true;
        }

        // No valid planting items were available.
        return false;
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
