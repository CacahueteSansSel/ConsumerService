package studios.nightek.consume.marketing;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class MarketingUtilities {
    public static void markItemAsBought(ItemStack target) {
        CompoundNBT nbt = target.getTag();
        if (nbt == null) nbt = new CompoundNBT();
        nbt.putBoolean("bought", true);
        target.setTag(nbt);
    }

    public static void markItemAsNotBought(ItemStack target) {
        CompoundNBT nbt = target.getTag();
        if (nbt == null) nbt = new CompoundNBT();
        nbt.putBoolean("bought", false);
        target.setTag(nbt);
    }

    public static boolean isItemBought(ItemStack item) {
        CompoundNBT nbt = item.getTag();

        if (nbt == null) return false;
        return nbt.contains("bought") && nbt.getBoolean("bought");
    }

    public static int getItemPrice(ItemStack item) {
        if (item.getTag() == null) return 0;

        CompoundNBT nbt = item.getTag();
        if (!nbt.contains("price")) return 0;

        return nbt.getInt("price");
    }

    public static ItemStack setItemPrice(ItemStack item, int price) {
        CompoundNBT nbt = item.getOrCreateTag();
        nbt.putInt("price", price);

        return item;
    }

    public static boolean isCommerceItem(ItemStack item) {
        //if (item.getTag() == null) return isCommerceItem(item.getItem());
        if (item.getTag() == null) return false;

        CompoundNBT nbt = item.getTag();
        return nbt.contains("price") && nbt.getInt("price") > 0;
    }

    /*public static boolean isCommerceItem(Item item) {
        return item instanceof ICommerce;
    }*/
}
