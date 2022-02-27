package studios.nightek.consume.items;


import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import studios.nightek.consume.ConsumerGroups;
import studios.nightek.consume.marketing.MarketingUtilities;

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