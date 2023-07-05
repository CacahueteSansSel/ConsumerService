package dev.cacahuete.consume.entities;

import dev.cacahuete.consume.ConsumerItems;
import dev.cacahuete.consume.ConsumerTileEntities;
import dev.cacahuete.consume.blocks.ItemStackHandlerWrapper;
import dev.cacahuete.consume.items.WrapperItem;
import dev.cacahuete.consume.marketing.MarketingUtilities;
import dev.cacahuete.consume.ui.ConsumerItemStackHandler;
import dev.cacahuete.consume.ui.PackagerContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeTileEntity;

import javax.annotation.Nullable;

public class PackagerTileEntity extends TileEntity implements IForgeTileEntity, INamedContainerProvider, ITickableTileEntity {

    public static final int STACK_COUNT = 3;
    ItemStackHandlerWrapper.Fingerprint lastFingerprint;
    ConsumerItemStackHandler inv;
    ItemStackHandlerWrapper wrap;

    public PackagerTileEntity() {
        super(ConsumerTileEntities.PACKAGER_TILE_ENTITY);

        inv = new ConsumerItemStackHandler(null, STACK_COUNT);
        wrap = new ItemStackHandlerWrapper(inv, this);
    }

    public void dropAll(World world, BlockPos pos) {
        InventoryHelper.dropInventoryItems(world, pos, wrap);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("item.consume.packager");
    }

    @Nullable
    @Override
    public Container createMenu(int winId, PlayerInventory plyInv, PlayerEntity plyEntity) {
        return PackagerContainer.newServer(winId, plyInv, inv, pos);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        CompoundNBT invNbt = inv.serializeNBT();
        compound.put("inventory", invNbt);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        inv.deserializeNBT(nbt.getCompound("inventory"));
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

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        write(nbt);

        return new SUpdateTileEntityPacket(getPos(), 0, nbt);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        read(state, tag);
    }

    public boolean allRequirementsMet() {
        return inv.getStackInSlot(0).getItem() == ConsumerItems.WRAPPER
                && WrapperItem.canBeWrapped(inv.getStackInSlot(1).getItem())
                && inv.getStackInSlot(2).isEmpty();
    }

    public boolean allSetPriceRequirementsMet() {
        return !inv.getStackInSlot(2).isEmpty() && WrapperItem.canBeWrapped(inv.getStackInSlot(2).getItem());
    }

    public void run() {
        ItemStack inputStack = wrap.getStackInSlot(1);

        wrap.setInventorySlotContents(2, WrapperItem.setWrapperItem(new ItemStack(ConsumerItems.WRAPPER, 1), inputStack));

        wrap.setInventorySlotContents(0, ItemStack.EMPTY);
        wrap.setInventorySlotContents(1, ItemStack.EMPTY);

        world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 1, 1);

        markDirty();
    }

    public void setPriceOfContainedItem(int price) {
        if (!allSetPriceRequirementsMet()) return;

        ItemStack item = wrap.getStackInSlot(2);
        MarketingUtilities.setItemPrice(item, price);
        wrap.setInventorySlotContents(2, item);

        world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1, 1);

        markDirty();
    }

    @Override
    public void tick() {
        if (world.isRemote) return; // Runs only on server

        if (allRequirementsMet()) run();
    }
}
