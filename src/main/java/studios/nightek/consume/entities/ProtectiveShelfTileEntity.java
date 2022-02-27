package studios.nightek.consume.entities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;
import studios.nightek.consume.ConsumerTileEntities;
import studios.nightek.consume.blocks.ItemStackHandlerWrapper;
import studios.nightek.consume.ui.ConsumerItemStackHandler;
import studios.nightek.consume.ui.ProtectiveShelfContainer;

import javax.annotation.Nullable;

public class ProtectiveShelfTileEntity extends TileEntity implements ConsumerItemStackHandler.IContentsChangedHandler, IForgeTileEntity, INamedContainerProvider, ITickableTileEntity {

    public static final int STACK_COUNT = 9;
    ItemStackHandlerWrapper.Fingerprint lastFingerprint;
    ConsumerItemStackHandler inv;
    ItemStackHandlerWrapper wrap;
    public ProtectiveShelfTileEntity() {
        super(ConsumerTileEntities.PROTECTIVE_SHELF_TILE_ENTITY);
        inv = new ConsumerItemStackHandler(this, STACK_COUNT);
        wrap = new ItemStackHandlerWrapper(inv, this);
    }

    public void dropAll(World world, BlockPos pos) {
        InventoryHelper.dropInventoryItems(world, pos, wrap);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("ui.consume.shop_shelf.title");
    }

    @Nullable
    @Override
    public Container createMenu(int winId, PlayerInventory plyInv, PlayerEntity plyEntity) {
        return ProtectiveShelfContainer.newServer(winId, plyInv, inv);
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

    public void inventoryChanged() {
        super.markDirty();

        world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
    }

    @Override
    public void tick() {
        if (world.isRemote) return; // Runs only on server

        // TODO: Remove that if onContentsChanged is working
    }

    @Override
    public void onContentsChanged(int slot) {
        if (world.isRemote) return; // Runs only on server

        inventoryChanged();
    }
}
