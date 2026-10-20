package com.example.simpleautofarm.blockentity;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.MenuProvider;

public abstract class MachineInventoryBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int INVENTORY_SIZE = 9;

    protected NonNullList<ItemStack> items =
        NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    protected MachineInventoryBlockEntity(
        net.minecraft.world.level.block.entity.BlockEntityType<?> type,
        net.minecraft.core.BlockPos pos,
        BlockState state
    ) {
        super(type, pos, state);
    }

    protected abstract boolean isItemValid(ItemStack stack);

    protected abstract boolean canAcceptFromSide(Direction side);
    protected abstract boolean canExtractFromSide(Direction side);

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= items.size()) {
            return ItemStack.EMPTY;
        }
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= items.size() || amount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack result = ContainerHelper.removeItem(items, slot, amount);

        if (!result.isEmpty()) {
            setChanged();
        }

        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0 || slot >= items.size()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= items.size()) {
            return;
        }

        if (!stack.isEmpty() && !isItemValid(stack)) {
            return;
        }

        if (!stack.isEmpty() && stack.getCount() > stack.getMaxStackSize()) {
            stack = stack.copy();
            stack.setCount(stack.getMaxStackSize());
        }

        items.set(slot, stack);
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot >= 0
            && slot < items.size()
            && !stack.isEmpty()
            && isItemValid(stack);
    }

    @Override
    public void clearContent() {
        items.clear();
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            items.add(ItemStack.EMPTY);
        }
        setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (!canAcceptFromSide(side) && !canExtractFromSide(side)) {
            return new int[0];
        }

        int[] slots = new int[getContainerSize()];

        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }

        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(
        int slot,
        ItemStack stack,
        Direction side
    ) {
        return canAcceptFromSide(side) && canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(
        int slot,
        ItemStack stack,
        Direction side
    ) {
        return slot >= 0
            && slot < getContainerSize()
            && canExtractFromSide(side)
            && !stack.isEmpty();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);

        // Discard invalid entries from old or malformed save data.
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);

            if (!stack.isEmpty() && !isItemValid(stack)) {
                items.set(i, ItemStack.EMPTY);
            } else if (!stack.isEmpty()
                    && stack.getCount() > stack.getMaxStackSize()) {
                stack.setCount(stack.getMaxStackSize());
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
    }

    protected final boolean canFitAll(List<ItemStack> incomingStacks) {
        ItemStack[] simulatedInventory = new ItemStack[getContainerSize()];

        // Copy the currentStack inventory so the check changes nothing.
        for (int i = 0; i < simulatedInventory.length; i++) {
            simulatedInventory[i] = getItem(i).copy();
        }

        // Simulate inserting every incoming stack.
        for (ItemStack incomingStack : incomingStacks) {
            if (incomingStack.isEmpty()) {
                continue;
            }

            if (!isItemValid(incomingStack)) {
                return false;
            }

            ItemStack remainingIncomingStack = incomingStack.copy();

            // First, fill compatible existing stacks.
            for (int i = 0; i < simulatedInventory.length; i++) {
                ItemStack currentStack = simulatedInventory[i];

                if (currentStack.isEmpty()
                        || !ItemStack.isSameItemSameComponents(
                                currentStack, remainingIncomingStack)) {
                    continue;
                }

                int limit = Math.min(
                    getMaxStackSize(),
                    currentStack.getMaxStackSize()
                );

                int space = limit - currentStack.getCount();

                if (space > 0) {
                    int moved = Math.min(space, remainingIncomingStack.getCount());

                    currentStack.grow(moved);
                    remainingIncomingStack.shrink(moved);
                }

                if (remainingIncomingStack.isEmpty()) {
                    break;
                }
            }

            // Then, use empty slots.
            for (int i = 0;
                    i < simulatedInventory.length && !remainingIncomingStack.isEmpty();
                    i++) {
                if (!simulatedInventory[i].isEmpty()) {
                    continue;
                }

                int limit = Math.min(
                    getMaxStackSize(),
                    remainingIncomingStack.getMaxStackSize()
                );

                int moved = Math.min(limit, remainingIncomingStack.getCount());

                simulatedInventory[i] = remainingIncomingStack.copyWithCount(moved);
                remainingIncomingStack.shrink(moved);
            }

            // Any remainder means the complete batch won't fit.
            if (!remainingIncomingStack.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
