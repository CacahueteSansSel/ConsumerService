package dev.cacahuete.consume.items;

import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import dev.cacahuete.consume.ConsumerGroups;
import dev.cacahuete.consume.marketing.MarketingUtilities;

public class CommerceAxeItem extends AxeItem {
    int emeraldPrice = 1;
    public CommerceAxeItem(String id, IItemTier tier, float attackDamageIn, float attackSpeedIn) {
        super(tier, attackDamageIn, attackSpeedIn, new Properties().group(ConsumerGroups.MAIN).maxStackSize(1));
        setRegistryName(id);
    }

    public CommerceAxeItem withPrice(int price) {
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
