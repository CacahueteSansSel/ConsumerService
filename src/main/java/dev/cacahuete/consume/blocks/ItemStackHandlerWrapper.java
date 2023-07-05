package dev.cacahuete.consume.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;

public class ItemStackHandlerWrapper implements IInventory {
    ItemStackHandler inv;
    TileEntity entity;

    public ItemStackHandlerWrapper(ItemStackHandler inv, @Nullable TileEntity entity) {
        this.inv = inv;
        this.entity = entity;
    }

    public Fingerprint getFingerprint() {
        return new Fingerprint(inv);
    }

    public boolean isSame(Fingerprint fingerprint) {
        return getFingerprint().equals(fingerprint);
    }

    @Override
    public int getSizeInventory() {
        return inv.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < inv.getSlots(); ++i) {
            if (!inv.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inv.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return inv.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        int maxPossibleItemStackSize = inv.getSlotLimit(index);
        return inv.extractItem(index, maxPossibleItemStackSize, false);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inv.setStackInSlot(index, stack);
    }

    @Override
    public void markDirty() {
        if (entity == null) return;
        entity.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < inv.getSlots(); ++i) {
            inv.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public static class Fingerprint {
        ArrayList<ItemStack> items = new ArrayList<>();

        public Fingerprint(ItemStackHandler handler) {
            for (int i = 0; i < handler.getSlots(); i++) {
                items.add(handler.getStackInSlot(i));
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            Fingerprint that = (Fingerprint) o;

            for (int i = 0; i < items.size(); i++) {
                if (i >= that.items.size()) return false;

                ItemStack otherItem = that.items.get(i);
                ItemStack item = items.get(i);

                if (otherItem.getItem() != item.getItem() || otherItem.getCount() != item.getCount()) return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(items);
        }
    }
}
