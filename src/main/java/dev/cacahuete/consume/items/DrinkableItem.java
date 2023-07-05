package dev.cacahuete.consume.items;

import net.minecraft.item.*;
import dev.cacahuete.consume.ConsumerGroups;

public class DrinkableItem extends CommerceItem {

    public DrinkableItem(String name, int saturation, int hunger) {
        super(new Properties().group(ConsumerGroups.FOOD).food(new Food.Builder().saturation(saturation).hunger(hunger).build()));
        setRegistryName(name);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }
}
