package studios.nightek.consume.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import studios.nightek.consume.ConsumerGroups;
import studios.nightek.consume.ConsumerItems;
import studios.nightek.consume.marketing.MarketingUtilities;

import javax.annotation.Nullable;
import java.util.List;

public class WrapperItem extends CommerceItem {
    public WrapperItem(Properties properties) {
        super(properties.group(ConsumerGroups.WRAPPERS));

        setRegistryName("wrapper");
    }

    public static boolean hasContainedItem(ItemStack wrappingItem) {
        return getContainedItem(wrappingItem) != null;
    }

    public static ItemStack getContainedItem(ItemStack wrappingItem) {
        if (wrappingItem.getCount() > 1) return null;

        CompoundNBT nbt = wrappingItem.getOrCreateTag();
        if (!nbt.contains("Item")) return null;

        ItemStack item = ItemStack.read(nbt.getCompound("Item"));
        return item;
    }

    @Override
    public int getPrice() {
        return 2;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new StringTextComponent(TextFormatting.LIGHT_PURPLE.toString() + stack.getTag() == null ? I18n.format("item.consume.empty_wrapper") : I18n.format("item.consume.wrapper"));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        ItemStack containedItem = getContainedItem(stack);
        if (containedItem != null) {
            tooltip.add(containedItem.getDisplayName());
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static ItemStack setWrapperItem(ItemStack wrappingItem, ItemStack innerItem) {
        if (wrappingItem.getCount() > 1) return wrappingItem;

        CompoundNBT nbt = wrappingItem.getOrCreateTag();
        CompoundNBT itemNbt = new CompoundNBT();
        innerItem.write(itemNbt);
        nbt.put("Item", itemNbt);

        return wrappingItem;
    }

    public static boolean canBeWrapped(Item item) {
        return item != Items.EMERALD
                && item != Items.EMERALD_BLOCK
                && item != Items.EMERALD_ORE
                && item != Items.AIR
                && !ConsumerItems.isItemFromMod(item);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (MarketingUtilities.isCommerceItem(context.getItem()) &&
                !MarketingUtilities.isItemBought(context.getItem())
                && !context.getPlayer().isCreative())
            return ActionResultType.FAIL;

        BlockPos pos = context.getPos();
        World worldIn = context.getWorld();
        PlayerEntity ply = context.getPlayer();
        CompoundNBT nbt = context.getItem().getTag();

        ItemStack finalItem = getContainedItem(context.getItem());
        if (finalItem == null || !canBeWrapped(finalItem.getItem())) return ActionResultType.FAIL;

        if (nbt != null) finalItem.setTag(nbt);

        ply.inventory.removeStackFromSlot(ply.inventory.getSlotFor(context.getItem())); // The Container Item must be alone in the slot !
        ply.inventory.addItemStackToInventory(finalItem);
        worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.PLAYERS, 1f, 1f, false);

        return ActionResultType.SUCCESS;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (!this.isInGroup(group)) return;

        items.add(new ItemStack(this, 1));

        for (Item item : Registry.ITEM) {
            if (!canBeWrapped(item)) continue;

            try {
                ItemStack wrapper = new ItemStack(this, 1);

                items.add(MarketingUtilities.setItemPrice(setWrapperItem(wrapper, new ItemStack(item, 1)), getPrice()));
            } catch (Exception ignored) {

            }
        }
    }
}
