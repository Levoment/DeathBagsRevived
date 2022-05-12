package com.github.levoment.deathbagsrevived;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class DeathBagInventory implements Inventory {

    private DefaultedList<ItemStack> inventory;

    public DeathBagInventory() {
        this.inventory = DefaultedList.ofSize(59, ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        boolean isEmpty = true;
        for (int i = 0; i < this.inventory.size(); i++) {
            if (!this.inventory.get(i).isEmpty()) isEmpty = false;
        }
        return isEmpty;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < this.inventory.size()) return this.inventory.get(slot);
        else return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stackToReturn;
        if (slot < this.inventory.size()) {
            if (this.inventory.get(slot).getCount() > amount) {
                this.inventory.get(slot).decrement(amount);
                return this.inventory.get(slot);
            } else {
                return this.removeStack(slot);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot < this.inventory.size()) {
            this.inventory.set(slot, ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < this.inventory.size()) {
            this.inventory.set(slot, stack);
        }
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.inventory.size(); i++) {
            this.inventory.remove(i);
        }
    }


}
