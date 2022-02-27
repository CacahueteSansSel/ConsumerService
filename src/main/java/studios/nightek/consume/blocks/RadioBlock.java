package studios.nightek.consume.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.Minecraft;
import net.minecraft.command.impl.StopSoundCommand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.tileentity.JukeboxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import studios.nightek.consume.entities.RadioBlockTileEntity;

import javax.annotation.Nullable;

public class RadioBlock extends Block {
    public final VoxelShape SHAPE = Block.makeCuboidShape(1, 0, 6, 15, 7, 11);

    public RadioBlock() {
        super(Properties.create(Material.IRON).notSolid().sound(SoundType.SCAFFOLDING).zeroHardnessAndResistance());
        setRegistryName("radio");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RadioBlockTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    float getMusicDiscFrequency(MusicDiscItem item) {
        return item.getComparatorValue() / 2 + 90.1f;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return ((RadioBlockTileEntity)worldIn.getTileEntity(pos)).getRecordForCurrentMusic().getComparatorValue();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
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

    /*
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            if (player.isSneaking()) {
                Minecraft.getInstance().getSoundHandler().stop(null, SoundCategory.RECORDS);
            }
            return ActionResultType.PASS;
        }
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof RadioBlockTileEntity)) return ActionResultType.FAIL;

        RadioBlockTileEntity radioTe = (RadioBlockTileEntity)te;

        if (player.isSneaking()) {
            player.sendStatusMessage(new TranslationTextComponent("block.consume.radio.paused"), true);
            return ActionResultType.SUCCESS;
        }

        MusicDiscItem disc = radioTe.next();
        player.sendStatusMessage(new TranslationTextComponent("block.consume.radio.frequency", getMusicDiscFrequency(disc)), true);
        return ActionResultType.SUCCESS;
    }
    */
}
