package dev.cacahuete.consume.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;

public class TVBlock extends Block {
    public static final DirectionProperty Facing = HorizontalBlock.HORIZONTAL_FACING;
    public final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(-2, 0, 7, 18, 16, 9);
    public final VoxelShape SHAPE_EAST = Block.makeCuboidShape(7, 0, -2, 9, 16, 18);

    public TVBlock() {
        super(Properties.create(Material.IRON).notSolid().sound(SoundType.SCAFFOLDING).zeroHardnessAndResistance());
        setRegistryName("television");
        setDefaultState(this.getDefaultState().with(Facing, Direction.NORTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(Facing)) {
            case NORTH:
            case SOUTH:
                return SHAPE_NORTH;
            case EAST:
            case WEST:
                return SHAPE_EAST;
        }
        return SHAPE_NORTH;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(Facing);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(Facing, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        return hasSolidSideOnTop(worldIn, blockpos) || hasEnoughSolidSide(worldIn, blockpos, Direction.UP);
    }
}
