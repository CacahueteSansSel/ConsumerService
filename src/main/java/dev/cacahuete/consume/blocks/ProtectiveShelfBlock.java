package dev.cacahuete.consume.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import dev.cacahuete.consume.entities.ProtectiveShelfTileEntity;

import javax.annotation.Nullable;

public class ProtectiveShelfBlock extends ContainerBlock {

    public final VoxelShape SHAPE_MIDDLE = Block.makeCuboidShape(6, 3, 0, 10, 16, 16);
    public final VoxelShape SHAPE_SHELF_BOTTOM = Block.makeCuboidShape(0, 0, 0, 16, 3, 16);
    public final VoxelShape SHAPE_SHELF_MIDDLE = Block.makeCuboidShape(0, 7, 0, 16, 8, 16);
    public final VoxelShape SHAPE_SHELF_TOP = Block.makeCuboidShape(0, 12, 0, 16, 13, 16);
    public final VoxelShape SHAPE = VoxelShapes.or(SHAPE_MIDDLE, VoxelShapes.or(SHAPE_SHELF_BOTTOM, VoxelShapes.or(SHAPE_SHELF_MIDDLE, SHAPE_SHELF_TOP)));

    public ProtectiveShelfBlock(String name) {
        super(Properties.create(Material.IRON).hardnessAndResistance(-1f, 3600000.0f)
                .sound(SoundType.METAL).notSolid());
        setRegistryName(name);
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
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ProtectiveShelfTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new ProtectiveShelfTileEntity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (worldIn.isRemote) return ActionResultType.SUCCESS; // on client side, don't do anything

        INamedContainerProvider namedContainerProvider = this.getContainer(state, worldIn, pos);
        if (namedContainerProvider != null) {
            if (!(player instanceof ServerPlayerEntity)) {
                return ActionResultType.FAIL;
            }

            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            NetworkHooks.openGui(serverPlayerEntity, namedContainerProvider, (packetBuffer)->{});
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = world.getTileEntity(blockPos);
            if (tileentity instanceof ProtectiveShelfTileEntity) {
                ProtectiveShelfTileEntity tileEntityInventoryBasic = (ProtectiveShelfTileEntity)tileentity;
                tileEntityInventoryBasic.dropAll(world, blockPos);
            }

            super.onReplaced(state, world, blockPos, newState, isMoving);
        }
    }
}