package studios.nightek.consume.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import studios.nightek.consume.accounting.AccountManager;
import studios.nightek.consume.marketing.MarketingUtilities;

import javax.annotation.Nullable;
import java.util.List;

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
