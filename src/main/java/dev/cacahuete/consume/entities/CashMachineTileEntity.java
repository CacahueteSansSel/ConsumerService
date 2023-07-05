package dev.cacahuete.consume.entities;

import dev.cacahuete.consume.accounting.AccountManager;
import dev.cacahuete.consume.blocks.ItemStackHandlerWrapper;
import dev.cacahuete.consume.items.ReceiptItem;
import dev.cacahuete.consume.marketing.MarketingUtilities;
import dev.cacahuete.consume.ui.CashMachineContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import net.minecraftforge.items.ItemStackHandler;
import dev.cacahuete.consume.ConsumerTileEntities;
import dev.cacahuete.consume.accounting.Account;

import javax.annotation.Nullable;
import java.util.UUID;

public class CashMachineTileEntity extends TileEntity implements IForgeTileEntity, INamedContainerProvider, ITickableTileEntity {

    public static final int STACK_COUNT = 13;
    public String displayName;
    ItemStackHandler inv;
    ItemStackHandlerWrapper wrap;
    public String targetAccountId;
    UUID ownerId;
    String ownerName;

    public CashMachineTileEntity() {
        super(ConsumerTileEntities.CASH_MACHINE_TILE_ENTITY);
        inv = new ItemStackHandler(STACK_COUNT);
        wrap = new ItemStackHandlerWrapper(inv, this);
        targetAccountId = null;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void dropAll(World world, BlockPos pos) {
        InventoryHelper.dropInventoryItems(world, pos, wrap);
    }

    public boolean isOwnedBy(PlayerEntity player) {
        if (ownerName == null || ownerId == null) return false;

        return player.getName().getString().equals(ownerName) && player.getUniqueID().equals(ownerId);
    }

    public boolean isNotOwned() {
        return ownerName == null || ownerId == null;
    }

    public void setOwner(PlayerEntity player) {
        ownerId = player.getUniqueID();
        ownerName = player.getName().getString();

        markDirty();
    }

    public void setTargetAccountId(String accountId) {
        targetAccountId = accountId;

        markDirty();
    }

    @Override
    public ITextComponent getDisplayName() {
        return displayName != null ? new StringTextComponent(displayName) : new TranslationTextComponent("ui.consume.cash_machine.title");
    }

    @Nullable
    @Override
    public Container createMenu(int winId, PlayerInventory plyInv, PlayerEntity plyEntity) {
        return CashMachineContainer.newServer(winId, plyInv, inv, pos);
    }

    public ReceiptItem.ReceiptContentBuilder buildReceiptContents() {
        ReceiptItem.ReceiptContentBuilder builder = new ReceiptItem.ReceiptContentBuilder();

        for (int i = 0; i < inv.getSlots() - 4; i++) {
            ItemStack item = inv.getStackInSlot(i);
            if (item.isEmpty() || MarketingUtilities.isItemBought(item)) continue;
            int price = MarketingUtilities.getItemPrice(item) * item.getCount();

            builder.append(item.getCount() + "x "
                    + item.getDisplayName().getString().toUpperCase()
                    + " : " + price
                    + " "
                    + AccountManager.CURRENCY_NAME);
        }

        return builder;
    }

    public int getTotalPriceInEmeralds() {
        int priceTotal = 0;

        for (int i = 0; i < inv.getSlots() - 4; i++) {
            ItemStack item = inv.getStackInSlot(i);
            if (item.isEmpty() || MarketingUtilities.isItemBought(item)) continue;

            priceTotal += MarketingUtilities.getItemPrice(item) * item.getCount();
        }

        return priceTotal;
    }

    public int getTotalInsertedEmeralds() {
        int priceTotal = 0;

        for (int i = 9; i < 13; i++) {
            ItemStack item = inv.getStackInSlot(i);
            if (item.getItem() == Items.EMERALD) {
                priceTotal += item.getCount();
            }
        }

        return priceTotal;
    }

    // This method is used by crypto transactions
    // in order to mark all inserted items as bought
    public void markItemsAsBought() {
        for (int i = 0; i < 9; i++) {
            ItemStack item = inv.getStackInSlot(i).getStack();

            if (MarketingUtilities.isCommerceItem(item))
                MarketingUtilities.markItemAsBought(item);
        }
    }

    public void proceedToPayment() {
        // Consume emeralds
        int toPayAmount = getTotalPriceInEmeralds();

        // If there is a target, send the money to it
        if (targetAccountId != null) {
            Account account = AccountManager.accessWalletUnsafe(targetAccountId);
            if (account == null) return;
            BankAccountControllerBlockTileEntity te = account.tileEntity;

            BankAccountControllerBlockTileEntity.TransactionResponse resp = te.beginVoidTransaction(toPayAmount);
            if (resp != BankAccountControllerBlockTileEntity.TransactionResponse.Success) return;
        }

        for (int i = 9; i < 13; i++) {
            if (toPayAmount <= 0) break;
            ItemStack slotStack = inv.getStackInSlot(i);
            if (toPayAmount > 64) {
                inv.setStackInSlot(i, ItemStack.EMPTY);
            } else {
                slotStack.setCount(slotStack.getCount() - toPayAmount);
                inv.setStackInSlot(i, slotStack);
            }
        }

        // Mark all inserted items as bought
        markItemsAsBought();
        markDirty();
    }

    public void setDisplayName(String name) {
        displayName = name;
        markDirty();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        CompoundNBT invNbt = inv.serializeNBT();
        compound.put("inventory", invNbt);

        if (ownerName != null) compound.putString("owner_name", ownerName);
        if (ownerId != null) compound.putUniqueId("owner_id", ownerId);
        if (displayName != null) compound.putString("display_name", displayName);
        if (targetAccountId != null) compound.putString("target", targetAccountId);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        inv.deserializeNBT(nbt.getCompound("inventory"));

        if (nbt.contains("owner_name"))
            ownerName = nbt.getString("owner_name");
        if (nbt.contains("owner_id"))
            ownerId = nbt.getUniqueId("owner_id");
        if (nbt.contains("display_name"))
            displayName = nbt.getString("display_name");
        if (nbt.contains("target"))
            targetAccountId = nbt.getString("target");
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        BlockState blockState = world.getBlockState(pos);
        read(blockState, pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        read(state, tag);
    }

    @Override
    public void tick() {
        if (world.isRemote) return;

        if (getTotalInsertedEmeralds() >= getTotalPriceInEmeralds()) proceedToPayment();
    }
}
