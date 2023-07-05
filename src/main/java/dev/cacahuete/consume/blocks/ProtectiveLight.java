package dev.cacahuete.consume.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class ProtectiveLight extends Block {
    public ProtectiveLight(String name) {
        super(Properties.create(Material.IRON).hardnessAndResistance(-1f, 3600000.0f)
                .sound(SoundType.GLASS).setLightLevel((b) -> 15));
        setRegistryName(name);
    }
}
