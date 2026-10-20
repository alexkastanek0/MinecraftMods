package com.example.simpleautofarm.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import com.example.simpleautofarm.PlanterHarvesterSimpleAutoFarm;
import com.example.simpleautofarm.menu.HarvesterMenu;
import com.example.simpleautofarm.menu.PlanterMenu;

public final class ModMenuTypes {
    public static final MenuType<PlanterMenu> PLANTER =
        register("planter", PlanterMenu::new);

    public static final MenuType<HarvesterMenu> HARVESTER =
        register("harvester", HarvesterMenu::new);

    private ModMenuTypes() {
    }

    private static <T extends AbstractContainerMenu> MenuType<T> register(
        String name,
        MenuType.MenuSupplier<T> constructor
    ) {
        return Registry.register(
            BuiltInRegistries.MENU,
            PlanterHarvesterSimpleAutoFarm.id(name),
            new MenuType<>(constructor, FeatureFlagSet.of())
        );
    }

    public static void initialize() {
        // Ensures the menu types are initialized.
    }
}
