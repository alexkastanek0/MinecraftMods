package com.example.simpleautofarm.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;

import com.example.simpleautofarm.PlanterHarvesterSimpleAutoFarm;
import com.example.simpleautofarm.registry.ModBlockEntities;
import com.example.simpleautofarm.farming.CropType;
import com.example.simpleautofarm.menu.HarvesterMenu;
import com.example.simpleautofarm.network.HarvesterAnimationNetworking;

import java.util.List;

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

    /**
     * Returns the crop type at the target if it is fully mature.
     */
    public CropType getMatureTargetCrop() {
        Level level = getLevel();

        if (level == null) {
            return null;
        }

        BlockPos cropPos = getTargetCropPos();
        BlockState cropState = level.getBlockState(cropPos);

        for (CropType crop : CropType.values()) {
            if (crop.isMature(cropState)) {
                return crop;
            }
        }

        return null;
    }

    /**
     * Attempts to harvest the mature crop directly in front
     * of this harvester.
     *
     * @return true if the crop was successfully harvested
     */
    public boolean tryHarvest() {
        try {
            Level level = getLevel();

            // All world changes must happen on the server.
            if (!(level instanceof ServerLevel serverLevel)) {
                return false;
            }

            BlockPos cropPos = getTargetCropPos();
            BlockState cropState = serverLevel.getBlockState(cropPos);

            // Only harvest one of our supported, mature crops.
            if (getMatureTargetCrop() == null) {
                return false;
            }

            // Generate the crop's normal loot exactly once.
            List<ItemStack> drops = Block.getDrops(
                cropState,
                serverLevel,
                cropPos,
                null
            );

            // Do not remove the crop if the drops are empty or
            // the complete batch cannot be stored.
            if (drops.isEmpty() || !canFitAll(drops)) {
                return false;
            }

            // Remove only the crop block. The farmland below remains.
            if (!serverLevel.setBlock(
                    cropPos,
                    Blocks.AIR.defaultBlockState(),
                    3
            )) {
                return false;
            }

            // Store the exact batch calculated above.
            if (!insertAll(drops)) {
                // Defensive rollback: restore the crop if insertion
                // unexpectedly fails after the successful capacity check.
                serverLevel.setBlock(cropPos, cropState, 3);
                return false;
            }

            HarvesterAnimationNetworking.sendAnimation(
                serverLevel,
                getBlockPos()
            );

            return true;
        } catch (Exception exception) {
            PlanterHarvesterSimpleAutoFarm.LOGGER.error(
                "Unexpected error while harvesting crop at {}",
                getBlockPos(),
                exception
            );

            throw exception;
        }
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return CropType.isValidHarvestedItem(stack);
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
