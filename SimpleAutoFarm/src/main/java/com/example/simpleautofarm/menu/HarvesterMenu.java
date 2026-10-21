package com.example.simpleautofarm.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import com.example.simpleautofarm.farming.CropType;
import com.example.simpleautofarm.registry.ModMenuTypes;

public class HarvesterMenu extends MachineMenu {
    public HarvesterMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(9));
    }

    public HarvesterMenu(
        int containerId,
        Inventory inventory,
        Container container
    ) {
        super(
            ModMenuTypes.HARVESTER,
            containerId,
            inventory,
            container,
            HarvesterMenu::isValidHarvestItem
        );
    }

    private static boolean isValidHarvestItem(ItemStack stack) {
        return CropType.isValidHarvestedItem(stack);
    }
}
