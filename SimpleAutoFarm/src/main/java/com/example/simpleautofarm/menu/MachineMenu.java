package com.example.simpleautofarm.menu;

import java.util.function.Predicate;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class MachineMenu extends AbstractContainerMenu {
    private static final int MACHINE_SLOT_COUNT = 9;

    private static final int MACHINE_START = 0;
    private static final int MACHINE_END = MACHINE_SLOT_COUNT;
    private static final int PLAYER_START = MACHINE_END;
    private static final int PLAYER_END =
        PLAYER_START + Inventory.INVENTORY_SIZE;

    private static final int MACHINE_X = 62;
    private static final int MACHINE_Y = 17;
    private static final int PLAYER_X = 8;
    private static final int PLAYER_Y = 84;
    private static final int SLOT_SIZE = 18;

    private final Container container;
    private final Predicate<ItemStack> itemValidator;

    protected MachineMenu(
        net.minecraft.world.inventory.MenuType<?> menuType,
        int containerId,
        Inventory playerInventory,
        Container container,
        Predicate<ItemStack> itemValidator
    ) {
        super(menuType, containerId);

        checkContainerSize(container, MACHINE_SLOT_COUNT);

        this.container = container;
        this.itemValidator = itemValidator;

        container.startOpen(playerInventory.player);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int slotIndex = column + row * 3;

                addSlot(new Slot(container, slotIndex,
                    MACHINE_X + column * SLOT_SIZE,
                    MACHINE_Y + row * SLOT_SIZE) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return MachineMenu.this.itemValidator.test(stack)
                            && super.mayPlace(stack);
                    }
                });
            }
        }

        addStandardInventorySlots(
            playerInventory,
            PLAYER_X,
            PLAYER_Y
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot slot = slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack originalStack = stack.copy();

        if (slotIndex < MACHINE_END) {
            if (!moveItemStackTo(
                    stack, PLAYER_START, PLAYER_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(
                    stack, MACHINE_START, MACHINE_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return originalStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
