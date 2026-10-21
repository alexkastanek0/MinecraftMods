package com.example.simpleautofarm.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import com.example.simpleautofarm.farming.CropType;
import com.example.simpleautofarm.registry.ModMenuTypes;

public class PlanterMenu extends MachineMenu {
    public PlanterMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(9));
    }

    public PlanterMenu(
        int containerId,
        Inventory inventory,
        Container container
    ) {
        super(
            ModMenuTypes.PLANTER,
            containerId,
            inventory,
            container,
            PlanterMenu::isValidSeed
        );
    }

    private static boolean isValidSeed(ItemStack stack) {
        return CropType.isValidPlantingItem(stack);
    }
}
