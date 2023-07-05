package dev.cacahuete.consume.entities;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import dev.cacahuete.consume.ConsumerTileEntities;

public class JukeboxSpeakerTileEntity extends TileEntity implements ITickableTileEntity {

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
    public static final int[] SOUNDS_DELAYS = new int[] {
            3700,
            3700,
            6900,
            3480,
            3940,
            1920,
            4620,
            2960,
            3000,
            3760,
            5020
    };
    int tickCounter = 0;
    int musicCounter = 0;

    public JukeboxSpeakerTileEntity() {
        super(ConsumerTileEntities.JUKEBOX_SPEAKER_TILE_ENTITY);
    }

    SoundEvent next() {
        musicCounter++;

        if (musicCounter >= SOUNDS.length) musicCounter = 0;

        markDirty();
        return SOUNDS[musicCounter];
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = super.write(compound);
        nbt.putInt("music", musicCounter);

        return nbt;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        musicCounter = nbt.getInt("music");
    }

    @Override
    public void tick() {
        if (world.isRemote) return;
        if (musicCounter >= SOUNDS.length) musicCounter = 0;

        if (!world.isBlockPowered(pos)) {
            tickCounter = -4;
            return;
        }

        if (tickCounter == -4) {
            SoundEvent nextEvent = SOUNDS[musicCounter];
            world.playSound(null, pos, nextEvent, SoundCategory.RECORDS, 1f, 1f);
            tickCounter = 0;
        }

        if (tickCounter >= SOUNDS_DELAYS[musicCounter]) {
            SoundEvent nextEvent = next();
            world.playSound(null, pos, nextEvent, SoundCategory.RECORDS, 1f, 1f);
            tickCounter = 0;
        } else tickCounter++;
    }
}
