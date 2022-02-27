package studios.nightek.consume.items;

import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import studios.nightek.consume.ConsumerGroups;
import studios.nightek.consume.marketing.MarketingUtilities;

public class CommercePickaxeItem extends PickaxeItem {
    int emeraldPrice = 1;
    public CommercePickaxeItem(String id, IItemTier tier, int attackDamageIn, float attackSpeedIn) {
        super(tier, attackDamageIn, attackSpeedIn, new Properties().group(ConsumerGroups.MAIN).maxStackSize(1));
        setRegistryName(id);
    }

    public CommercePickaxeItem withPrice(int price) {
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