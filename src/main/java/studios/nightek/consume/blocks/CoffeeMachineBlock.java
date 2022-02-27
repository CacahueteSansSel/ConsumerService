package studios.nightek.consume.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class CoffeeMachineBlock extends Block {
    public static final DirectionProperty Facing = HorizontalBlock.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(3, 0, 3, 13, 13, 13);

    public CoffeeMachineBlock() {
        super(Properties.create(Material.IRON).hardnessAndResistance(3.5f));
        setRegistryName("coffee_machine");

        setDefaultState(this.getDefaultState().with(Facing, Direction.NORTH));
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
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE_NORTH;
    }
}
