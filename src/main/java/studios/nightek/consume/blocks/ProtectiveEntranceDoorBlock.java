package studios.nightek.consume.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ProtectiveEntranceDoorBlock extends DoorBlock {
    public ProtectiveEntranceDoorBlock() {
        super(Properties.create(Material.IRON).hardnessAndResistance(-1f, 3600000.0f)
                .sound(SoundType.METAL).notSolid());
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(OPEN, Boolean.valueOf(true)).with(HINGE, DoorHingeSide.LEFT).with(POWERED, Boolean.valueOf(false)).with(HALF, DoubleBlockHalf.LOWER));
        setRegistryName("protective_entrance_door");
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean redstone = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (blockIn != this && redstone != state.get(POWERED)) {
            if (redstone != (!state.get(OPEN))) {
                // WTF are these numbers ?!
                worldIn.playEvent((PlayerEntity)null, redstone ? 1005 : 1011, pos, 0);
            }

            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(redstone)).with(OPEN, !Boolean.valueOf(redstone)), 2);
        }
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        if (blockpos.getY() < 255 && context.getWorld().getBlockState(blockpos.up()).isReplaceable(context)) {
            World world = context.getWorld();
            boolean flag = !world.isBlockPowered(blockpos) && !world.isBlockPowered(blockpos.up());
            return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(HINGE, this.getHingeSide(context)).with(POWERED, !Boolean.valueOf(flag)).with(OPEN, Boolean.valueOf(flag)).with(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    private DoorHingeSide getHingeSide(BlockItemUseContext context) {
        IBlockReader iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        Direction direction = context.getPlacementHorizontalFacing();
        BlockPos blockpos1 = blockpos.up();
        Direction direction1 = direction.rotateYCCW();
        BlockPos blockpos2 = blockpos.offset(direction1);
        BlockState blockstate = iblockreader.getBlockState(blockpos2);
        BlockPos blockpos3 = blockpos1.offset(direction1);
        BlockState blockstate1 = iblockreader.getBlockState(blockpos3);
        Direction direction2 = direction.rotateY();
        BlockPos blockpos4 = blockpos.offset(direction2);
        BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
        BlockPos blockpos5 = blockpos1.offset(direction2);
        BlockState blockstate3 = iblockreader.getBlockState(blockpos5);
        int i = (blockstate.hasOpaqueCollisionShape(iblockreader, blockpos2) ? -1 : 0) + (blockstate1.hasOpaqueCollisionShape(iblockreader, blockpos3) ? -1 : 0) + (blockstate2.hasOpaqueCollisionShape(iblockreader, blockpos4) ? 1 : 0) + (blockstate3.hasOpaqueCollisionShape(iblockreader, blockpos5) ? 1 : 0);
        boolean flag = blockstate.matchesBlock(this) && blockstate.get(HALF) == DoubleBlockHalf.LOWER;
        boolean flag1 = blockstate2.matchesBlock(this) && blockstate2.get(HALF) == DoubleBlockHalf.LOWER;
        if ((!flag || flag1) && i <= 0) {
            if ((!flag1 || flag) && i >= 0) {
                int j = direction.getXOffset();
                int k = direction.getZOffset();
                Vector3d vector3d = context.getHitVec();
                double d0 = vector3d.x - (double)blockpos.getX();
                double d1 = vector3d.z - (double)blockpos.getZ();
                return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            } else {
                return DoorHingeSide.LEFT;
            }
        } else {
            return DoorHingeSide.RIGHT;
        }
    }
}
