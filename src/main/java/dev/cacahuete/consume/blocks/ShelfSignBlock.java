package dev.cacahuete.consume.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockVoxelShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;

public class ShelfSignBlock extends Block {
    public static final DirectionProperty Facing = HorizontalBlock.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE_NS_MAIN = Block.makeCuboidShape(1, 0, 7, 15, 10, 9);
    public static final VoxelShape SHAPE_NS_LEFT = Block.makeCuboidShape(3, 10, 7, 4, 16, 9);
    public static final VoxelShape SHAPE_NS_RIGHT = Block.makeCuboidShape(12, 10, 7, 13, 16, 9);
    public static final VoxelShape SHAPE_NS = VoxelShapes.or(SHAPE_NS_MAIN, VoxelShapes.or(SHAPE_NS_LEFT, SHAPE_NS_RIGHT));

    public static final VoxelShape SHAPE_EO_MAIN = Block.makeCuboidShape(7, 0, 1, 9, 10, 15);
    public static final VoxelShape SHAPE_EO_LEFT = Block.makeCuboidShape(7, 10, 3, 9, 16, 4);
    public static final VoxelShape SHAPE_EO_RIGHT = Block.makeCuboidShape(7, 10, 12, 9, 16, 13);
    public static final VoxelShape SHAPE_EO = VoxelShapes.or(SHAPE_EO_MAIN, VoxelShapes.or(SHAPE_EO_LEFT, SHAPE_EO_RIGHT));

    public ShelfSignBlock(String name) {
        super(AbstractBlock.Properties.create(Material.IRON).sound(SoundType.SCAFFOLDING).notSolid());
        setRegistryName("shelf_sign_" + name);
        setDefaultState(this.getDefaultState().with(Facing, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(Facing)) {
            case NORTH:
            case SOUTH:
                return SHAPE_NS;
            case EAST:
            case WEST:
                return SHAPE_EO;
        }

        return SHAPE_NS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(Facing, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(Facing);
    }
}
