package studios.nightek.consume.items;

import net.minecraft.item.Item;
import studios.nightek.consume.ConsumerGroups;

public class RootkitItem extends Item {
    public RootkitItem() {
        super(new Properties().group(ConsumerGroups.MAIN).maxStackSize(1));
        setRegistryName("rootkit");
    }
}
