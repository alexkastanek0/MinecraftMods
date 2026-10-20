package com.example.simpleautofarm.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
        return stack.is(Items.WHEAT_SEEDS)
            || stack.is(Items.CARROT)
            || stack.is(Items.POTATO)
            || stack.is(Items.BEETROOT_SEEDS);
    }
}
