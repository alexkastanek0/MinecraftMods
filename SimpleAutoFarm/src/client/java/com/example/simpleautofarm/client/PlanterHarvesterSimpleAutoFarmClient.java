package com.example.simpleautofarm.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

import com.example.simpleautofarm.registry.ModMenuTypes;

public class PlanterHarvesterSimpleAutoFarmClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		MenuScreens.register(
            ModMenuTypes.PLANTER,
            PlanterScreen::new
        );

        MenuScreens.register(
            ModMenuTypes.HARVESTER,
            HarvesterScreen::new
        );
	}
}