package com.example.simpleautofarm.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import com.example.simpleautofarm.menu.PlanterMenu;

public class PlanterScreen extends MachineScreen<PlanterMenu> {

    public PlanterScreen(
        PlanterMenu menu,
        Inventory inventory,
        Component title
    ) {
        super(menu, inventory, title);
    }
}