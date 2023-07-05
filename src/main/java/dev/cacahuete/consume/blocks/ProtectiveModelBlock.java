package dev.cacahuete.consume.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class ProtectiveModelBlock extends Block {
    VoxelShape shape;
    VoxelShape collShape;

    public ProtectiveModelBlock(String name) {
        super(Properties.create(Material.IRON).hardnessAndResistance(-1f, 3600000.0f)
                .sound(SoundType.METAL).notSolid());
        setRegistryName(name);
    }

    public ProtectiveModelBlock withShape(double x, double y, double z, double x2, double y2, double z2) {
        shape = Block.makeCuboidShape(x, y, z, x2, y2, z2);
        return this;
    }

    public ProtectiveModelBlock withCollShape(double x, double y, double z, double x2, double y2, double z2) {
        collShape = Block.makeCuboidShape(x, y, z, x2, y2, z2);
        return this;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return shape;
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos) {
        return collShape;
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return shape;
    }

    public boolean isTransparent(BlockState state) {
        return true;
    }
}