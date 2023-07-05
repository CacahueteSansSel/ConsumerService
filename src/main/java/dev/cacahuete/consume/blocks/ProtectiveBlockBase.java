package dev.cacahuete.consume.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class ProtectiveBlockBase extends Block {
    public ProtectiveBlockBase(String name) {
        super(Properties.create(Material.IRON).hardnessAndResistance(-1f, 3600000.0f).sound(SoundType.METAL));
        setRegistryName(name);
    }
}
