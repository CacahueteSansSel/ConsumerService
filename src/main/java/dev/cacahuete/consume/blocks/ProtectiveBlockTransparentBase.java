package dev.cacahuete.consume.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ProtectiveBlockTransparentBase extends AbstractGlassBlock {

    public ProtectiveBlockTransparentBase(String name) {
        super(AbstractBlock.Properties.create(Material.IRON)
                .hardnessAndResistance(-1f, 3600000.0f)
                .sound(SoundType.GLASS)
                .notSolid()
                .setAllowsSpawn((state, reader, pos, entity) -> false)
                .setOpaque((state, reader, pos) -> false)
                .setSuffocates((state, reader, pos) -> false)
                .setBlocksVision((state, reader, pos) -> false));

        setRegistryName(name);
    }
}
