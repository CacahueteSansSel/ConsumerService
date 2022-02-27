package studios.nightek.consume.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import studios.nightek.consume.ConsumerItems;
import studios.nightek.consume.entities.CashMachineTileEntity;
import studios.nightek.consume.items.CreditCardItem;

import javax.annotation.Nullable;

public class ProtectiveCashMachineBlock extends ContainerBlock {
    public static final DirectionProperty Facing = HorizontalBlock.HORIZONTAL_FACING;

    public ProtectiveCashMachineBlock() {
        super(Properties.create(Material.IRON).hardnessAndResistance(-1f, 3600000.0f)
                .sound(SoundType.METAL).notSolid());
        setRegistryName("cash_machine");
        setDefaultState(this.getDefaultState().with(Facing, Direction.NORTH));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CashMachineTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new CashMachineTileEntity();
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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isRemote || !(placer instanceof ServerPlayerEntity)) return;
        CashMachineTileEntity te = (CashMachineTileEntity)worldIn.getTileEntity(pos);

        ServerPlayerEntity player = (ServerPlayerEntity)placer;
        te.setOwner(player);
        player.sendStatusMessage(new StringTextComponent("You are now the owner of the machine !"), true);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (worldIn.isRemote) return ActionResultType.SUCCESS; // Server side only

        INamedContainerProvider namedContainerProvider = this.getContainer(state, worldIn, pos);
        if (namedContainerProvider != null) {
            ItemStack curItem = player.inventory.getCurrentItem();
            CashMachineTileEntity te = ((CashMachineTileEntity)namedContainerProvider);

            if (curItem.getItem() == Items.NAME_TAG && curItem.hasDisplayName()) {
                if (te.isNotOwned()) te.setOwner(player);

                if (!te.isOwnedBy(player)) {
                    player.sendStatusMessage(new StringTextComponent("You are not the owner of the machine ! The owner is " + te.getOwnerName()), true);
                    return ActionResultType.FAIL;
                }
                te.setDisplayName(curItem.getDisplayName().getString());

                player.sendStatusMessage(new StringTextComponent("Set machine's display name to '" + te.displayName + "'"), true);
                return ActionResultType.SUCCESS;
            } else if (curItem.getItem() == ConsumerItems.CREDIT_CARD) {
                if (te.isNotOwned()) te.setOwner(player);

                if (!te.isOwnedBy(player)) {
                    player.sendStatusMessage(new StringTextComponent("You are not the owner of the machine ! The owner is " + te.getOwnerName()), true);
                    return ActionResultType.FAIL;
                }
                te.setTargetAccountId(CreditCardItem.getCardWalletId(curItem));

                player.sendStatusMessage(new StringTextComponent("Set machine's target account to '" + te.targetAccountId + "'"), true);
                return ActionResultType.SUCCESS;
            }
            if (!(player instanceof ServerPlayerEntity)) {
                return ActionResultType.FAIL;
            }

            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            NetworkHooks.openGui(serverPlayerEntity, namedContainerProvider, (packetBuffer) -> {
                packetBuffer.writeInt(pos.getX());
                packetBuffer.writeInt(pos.getY());
                packetBuffer.writeInt(pos.getZ());
            });
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = world.getTileEntity(blockPos);
            if (tileentity instanceof CashMachineTileEntity) {
                CashMachineTileEntity tileEntityInventoryBasic = (CashMachineTileEntity)tileentity;
                tileEntityInventoryBasic.dropAll(world, blockPos);
            }

            super.onReplaced(state, world, blockPos, newState, isMoving);
        }
    }
}
