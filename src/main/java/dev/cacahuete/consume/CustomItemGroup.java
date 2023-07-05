package dev.cacahuete.consume;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class CustomItemGroup extends ItemGroup {
    Supplier<ItemStack> iconSupplier;
    boolean searchBar = false;

    public CustomItemGroup(String name, Supplier<ItemStack> supplier) {
        super("consume." + name);
        iconSupplier = supplier;

        if (searchBar) setBackgroundImageName("item_search.png");
    }

    public CustomItemGroup withSearchBar() {
        searchBar = true;
        return this;
    }

    @Override
    public boolean hasSearchBar() {
        return searchBar;
    }

    public ItemStack createIcon() {
        return iconSupplier.get();
    }
}
