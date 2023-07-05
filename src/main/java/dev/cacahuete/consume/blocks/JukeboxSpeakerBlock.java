package dev.cacahuete.consume.blocks;

import dev.cacahuete.consume.entities.JukeboxSpeakerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class JukeboxSpeakerBlock extends Block {
    public static final DirectionProperty Facing = HorizontalBlock.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(4, 1, 7, 12, 12, 16);
    public static final VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(4, 1, 0, 12, 12, 9);
    public static final VoxelShape SHAPE_WEST = Block.makeCuboidShape(7, 1, 4, 16, 12, 12);
    public static final VoxelShape SHAPE_EAST = Block.makeCuboidShape(0, 1, 4, 9, 12, 12);

    public JukeboxSpeakerBlock() {
        super(Properties.create(Material.IRON).notSolid().sound(SoundType.SCAFFOLDING));
        setRegistryName("jukebox_speaker");
        setDefaultState(this.getDefaultState().with(Facing, Direction.NORTH));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(Facing);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(Facing)) {
            case WEST:
                return SHAPE_WEST;
            case EAST:
                return SHAPE_EAST;
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
        }

        return SHAPE_SOUTH;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(Facing, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new JukeboxSpeakerTileEntity();
    }
}
