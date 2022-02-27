package studios.nightek.consume.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class PlateBlock extends Block {
    public static final VoxelShape SHAPE = Block.makeCuboidShape(4, 0, 4, 12, 1, 12);

    public PlateBlock() {
        super(Properties.create(Material.IRON).notSolid().sound(SoundType.SCAFFOLDING).zeroHardnessAndResistance());
        setRegistryName("plate");
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        return hasSolidSideOnTop(worldIn, blockpos) || hasEnoughSolidSide(worldIn, blockpos, Direction.UP);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
}
