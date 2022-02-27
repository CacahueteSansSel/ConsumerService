package studios.nightek.consume.entities;

import net.minecraft.block.BlockState;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.util.Constants;
import studios.nightek.consume.ConsumerTileEntities;

import javax.annotation.Nullable;

public class RadioBlockTileEntity extends TileEntity {

    public static final SoundEvent[] SOUNDS = new SoundEvent[] {
            SoundEvents.MUSIC_DISC_CAT,
            SoundEvents.MUSIC_DISC_CHIRP,
            SoundEvents.MUSIC_DISC_BLOCKS,
            SoundEvents.MUSIC_DISC_FAR,
            SoundEvents.MUSIC_DISC_MALL,
            SoundEvents.MUSIC_DISC_MELLOHI,
            SoundEvents.MUSIC_DISC_WAIT,
            SoundEvents.MUSIC_DISC_PIGSTEP,
            SoundEvents.MUSIC_DISC_STAL,
            SoundEvents.MUSIC_DISC_STRAD,
            SoundEvents.MUSIC_DISC_WARD
    };

    public int musIndex;

    public RadioBlockTileEntity() {
        super(ConsumerTileEntities.RADIO_TILE_ENTITY);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("index", musIndex);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        musIndex = nbt.getInt("index");
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = new CompoundNBT();
        write(nbt);
        return nbt;
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
        super.handleUpdateTag(state, tag);
        read(state, tag);
    }

    public MusicDiscItem getRecordForCurrentMusic() {
        return MusicDiscItem.getBySound(SOUNDS[musIndex]);
    }

    public MusicDiscItem next() {
        musIndex++;
        if (musIndex >= SOUNDS.length) musIndex = 0;
        world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SOUNDS[musIndex], SoundCategory.RECORDS, 1f, 1f, false);
        markDirty();

        return getRecordForCurrentMusic();
    }
}
