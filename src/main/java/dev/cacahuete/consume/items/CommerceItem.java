package dev.cacahuete.consume.items;

import dev.cacahuete.consume.marketing.MarketingUtilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CommerceItem extends Item {

    public int emeraldCount;

    public CommerceItem(Properties prop) {
        super(prop);
        emeraldCount = 1;
    }

    public CommerceItem withPrice(int priceEmerald) {
        emeraldCount = priceEmerald;
        return this;
    }

    public int getPrice() {
        return emeraldCount;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(MarketingUtilities.setItemPrice(new ItemStack(this), emeraldCount));
        }
    }
}
