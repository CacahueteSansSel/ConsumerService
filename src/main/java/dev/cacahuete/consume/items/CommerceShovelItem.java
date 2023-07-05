package dev.cacahuete.consume.items;


import dev.cacahuete.consume.marketing.MarketingUtilities;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import dev.cacahuete.consume.ConsumerGroups;

public class CommerceShovelItem extends ShovelItem {
    int emeraldPrice = 1;
    public CommerceShovelItem(String id, IItemTier tier, float attackDamageIn, float attackSpeedIn) {
        super(tier, attackDamageIn, attackSpeedIn, new Properties().group(ConsumerGroups.MAIN).maxStackSize(1));
        setRegistryName(id);
    }

    public CommerceShovelItem withPrice(int price) {
        emeraldPrice = price;

        return this;
    }

    public int getPrice() {
        return emeraldPrice;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(MarketingUtilities.setItemPrice(new ItemStack(this), getPrice()));
        }
    }
}