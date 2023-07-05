package dev.cacahuete.consume.items;

import net.minecraft.item.Item;
import dev.cacahuete.consume.ConsumerGroups;

public class RootkitItem extends Item {
    public RootkitItem() {
        super(new Properties().group(ConsumerGroups.MAIN).maxStackSize(1));
        setRegistryName("rootkit");
    }
}
