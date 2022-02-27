package studios.nightek.consume.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RedstoneActivatedBlock extends ProtectiveBlockBase {
    boolean lastPowered = false;

    public RedstoneActivatedBlock(String name) {
        super(name);
    }

    public void onRedstoneActivate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {

    }

    public void onRedstoneChange(boolean isOn, BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (isOn) onRedstoneActivate(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.isRemote) return;
        boolean power = worldIn.isBlockPowered(pos);
        if (power != lastPowered) {
            onRedstoneChange(power, state, worldIn, pos, blockIn, fromPos, isMoving);
            lastPowered = power;
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }
}
