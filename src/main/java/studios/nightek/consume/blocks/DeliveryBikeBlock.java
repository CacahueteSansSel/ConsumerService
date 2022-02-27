package studios.nightek.consume.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import studios.nightek.consume.ConsumerBlocks;
import studios.nightek.consume.ConsumerItems;
import studios.nightek.consume.items.ContainerFoodItemCouple;
import studios.nightek.consume.items.IItemCouple;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DeliveryBikeBlock extends Block {
    public static final DirectionProperty Facing = HorizontalBlock.HORIZONTAL_FACING;
    public final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(3, 0, 0, 13, 13, 16);
    public final VoxelShape SHAPE_EAST = Block.makeCuboidShape(0, 0, 3, 16, 13, 13);

    public DeliveryBikeBlock() {
        super(Properties.create(Material.IRON).notSolid().hardnessAndResistance(2)
                .setOpaque((a, b, c) -> false));

        setRegistryName("delivery_bike");
        setDefaultState(this.getDefaultState().with(Facing, Direction.NORTH));
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
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        ArrayList<ItemStack> items = new ArrayList<>();

        Random rng = builder.getWorld().getRandom();
        int cardboardCount = 0;

        for (int i = 0; i < rng.nextInt(5); i++) {
            for (Block block : ConsumerBlocks.all()) {
                if (!(block instanceof CardboardBlock)) continue;
                CardboardBlock cardboardBlock = (CardboardBlock)block;

                float percent = rng.nextFloat();
                if (percent >= cardboardBlock.chance) continue;

                items.add(new ItemStack(ConsumerBlocks.getItemForBlock(cardboardBlock), 1));
                cardboardCount++;
                break;
            }
        }

        IItemCouple[] couples = ConsumerItems.allCouples();

        for (int i = 0; i < rng.nextInt(5 - cardboardCount); i++) {
            IItemCouple couple = couples[rng.nextInt(couples.length)];

            if (rng.nextFloat() > 0.25f) continue;

            items.add(new ItemStack(couple.all()[0], 1));
        }

        return items;
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
}
