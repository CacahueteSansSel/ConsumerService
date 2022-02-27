package studios.nightek.consume.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import studios.nightek.consume.ConsumerBlocks;
import studios.nightek.consume.ConsumerItems;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CardboardBlock extends Block {
    public static final DirectionProperty Facing = HorizontalBlock.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(2, 0, 0, 14, 10, 16);
    public static final VoxelShape SHAPE_EAST = Block.makeCuboidShape(0, 0, 2, 16, 10, 14);
    Supplier<BlockState> blockStateSupplier;
    public int price;
    public float chance = 1;

    public CardboardBlock() {
        super(Properties.create(Material.WOOD).hardnessAndResistance(2f));
        setRegistryName("cardboard_empty");

        blockStateSupplier = null;
        setDefaultState(this.getDefaultState().with(Facing, Direction.NORTH));
    }

    public CardboardBlock(String name, Supplier<BlockState> blockStateSupplier) {
        super(Properties.create(Material.WOOD).hardnessAndResistance(3f));
        setRegistryName("cardboard_" + name);
        this.blockStateSupplier = blockStateSupplier;
        setDefaultState(this.getDefaultState().with(Facing, Direction.NORTH));
    }

    public CardboardBlock withChance(float chance) {
        this.chance = chance;

        return this;
    }

    public CardboardBlock withPrice(int price) {
        this.price = price;

        return this;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        ArrayList<ItemStack> items = new ArrayList<>();
        if (blockStateSupplier == null) {
            items.add(new ItemStack(ConsumerBlocks.getItemForBlock(this), 1));
            return items;
        }

        items.add(new ItemStack(ConsumerBlocks.getItemForBlock(ConsumerBlocks.EMPTY_CARDBOARD), 1));
        items.add(new ItemStack(ConsumerBlocks.getItemForBlock(blockStateSupplier.get().getBlock()), 1));

        return items;
    }

    @Override
    public String getTranslationKey() {
        return "block.consume.cardboard";
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (blockStateSupplier != null) {
            IFormattableTextComponent c = blockStateSupplier.get().getBlock().getTranslatedName();
            tooltip.add(c);
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
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
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (blockStateSupplier == null) return ActionResultType.PASS;

        if (player.inventory.getCurrentItem().getItem() != ConsumerItems.SWISS_KNIFE) {
            player.sendStatusMessage(new TranslationTextComponent("block.consume.cardboard.swiss_knife"), true);
            return ActionResultType.PASS;
        }

        BlockState targetState;
        try {
            targetState = blockStateSupplier.get().with(Facing, state.get(Facing));
        } catch (IllegalArgumentException ex) {
            targetState = blockStateSupplier.get();
        }

        worldIn.setBlockState(pos, targetState);
        return ActionResultType.SUCCESS;
    }
}
