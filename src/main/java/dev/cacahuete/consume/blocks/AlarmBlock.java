package dev.cacahuete.consume.blocks;

import net.minecraft.block.*;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import dev.cacahuete.consume.ConsumerSounds;

public class AlarmBlock extends RedstoneActivatedBlock {
    public AlarmBlock() {
        super("alarm");
    }

    @Override
    public void onRedstoneActivate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        worldIn.playSound(null, pos, ConsumerSounds.ALARM_BEEP, SoundCategory.BLOCKS, 1f, 1f);
    }
}
