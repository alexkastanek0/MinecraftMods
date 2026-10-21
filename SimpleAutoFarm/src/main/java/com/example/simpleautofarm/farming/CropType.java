package com.example.simpleautofarm.farming;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public enum CropType {
    WHEAT(
        Items.WHEAT_SEEDS,
        List.of(
            Items.WHEAT, Items.WHEAT_SEEDS
        ),
        Blocks.WHEAT
    ),
    CARROTS(
        Items.CARROT,
        List.of(
            Items.CARROT
        ),
        Blocks.CARROTS
    ),
    POTATOES(
        Items.POTATO,
        List.of(
            Items.POTATO, Items.POISONOUS_POTATO
        ),
        Blocks.POTATOES
    ),
    BEETROOT(
        Items.BEETROOT_SEEDS,
        List.of(
            Items.BEETROOT, Items.BEETROOT_SEEDS
        ),
        Blocks.BEETROOTS
    );

    private final Item plantingItem;
    private final List<Item> harvestedItems;
    private final Block cropBlock;

    CropType(Item plantingItem, List<Item> harvestedItems, Block cropBlock) {
        this.plantingItem = plantingItem;
        this.harvestedItems = List.copyOf(harvestedItems);
        this.cropBlock = cropBlock;
    }

    public Item getPlantingItem() {
        return plantingItem;
    }

    public List<Item> getHarvestedItems() {
        return harvestedItems;
    }

    public Block getCropBlock() {
        return cropBlock;
    }

    /**
     * Returns whether this crop state is fully mature.
     */
    public boolean isMature(BlockState state) {
        return state.is(cropBlock)
            && state.getBlock() instanceof CropBlock crop
            && crop.isMaxAge(state);
    }

    public boolean matchesPlantingItem(ItemStack stack) {
        return !stack.isEmpty() && stack.is(plantingItem);
    }

    public boolean matchesHarvestedItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        for (Item harvestedItem : harvestedItems) {
            if (stack.is(harvestedItem)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isValidPlantingItem(ItemStack stack) {
        for (CropType crop : values()) {
            if (crop.matchesPlantingItem(stack)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isValidHarvestedItem(ItemStack stack) {
        for (CropType crop : values()) {
            if (crop.matchesHarvestedItem(stack)) {
                return true;
            }
        }

        return false;
    }

    public static CropType fromPlantingItem(ItemStack stack) {
        for (CropType crop : values()) {
            if (crop.matchesPlantingItem(stack)) {
                return crop;
            }
        }

        return null;
    }
}
