package com.example.simpleautofarm.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import com.example.simpleautofarm.menu.HarvesterMenu;

public class HarvesterScreen extends MachineScreen<HarvesterMenu> {

    public HarvesterScreen(
        HarvesterMenu menu,
        Inventory inventory,
        Component title
    ) {
        super(menu, inventory, title);
    }
}