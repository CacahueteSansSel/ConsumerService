package dev.cacahuete.consume.items;

import dev.cacahuete.consume.marketing.MarketingUtilities;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import dev.cacahuete.consume.ConsumerGroups;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class DiscSleeveItem extends CommerceItem {
    MusicDiscItem item;
    Supplier<Item>[] additionalItems;

    public DiscSleeveItem() {
        super(new Properties().group(ConsumerGroups.MAIN));

        setRegistryName("disc_sleeve_empty");
    }

    public DiscSleeveItem(MusicDiscItem item, String name, @Nullable Supplier<Item>... additionalItems) {
        super(new Properties().group(ConsumerGroups.MAIN).maxStackSize(1));
        this.item = item;
        this.additionalItems = additionalItems;
        setRegistryName("disc_sleeve_" + name);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (item == null) {
            super.addInformation(stack, worldIn, tooltip, flagIn);
            return;
        }

        tooltip.add(item.getDescription());
        if (additionalItems.length > 0) tooltip.add(new TranslationTextComponent("item.consume.disc_sleeve.additional_items", additionalItems.length));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if (item == null) {
            return new TranslationTextComponent("item.consume.disc_sleeve_empty");
        }

        return new StringTextComponent(TextFormatting.LIGHT_PURPLE.toString() + I18n.format("item.consume.disc_sleeve"));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (item == null) return ActionResultType.PASS;

        if (MarketingUtilities.isCommerceItem(context.getItem()) &&
                !MarketingUtilities.isItemBought(context.getItem())
                && !context.getPlayer().isCreative())
            return ActionResultType.FAIL;

        BlockPos pos = context.getPos();
        World worldIn = context.getWorld();
        PlayerEntity ply = context.getPlayer();
        CompoundNBT nbt = context.getItem().getTag();
        ItemStack finalItem = new ItemStack(item, 1);
        if (nbt != null) finalItem.setTag(nbt);
        ply.inventory.removeStackFromSlot(ply.inventory.getSlotFor(context.getItem())); // The Container Item must be alone in the slot !
        ply.inventory.addItemStackToInventory(finalItem);
        worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1f, 1f, false);
        if (additionalItems != null) {
            for (int i = 0; i < additionalItems.length; i++) {
                ply.inventory.addItemStackToInventory(new ItemStack(additionalItems[i].get(), 1));
            }
        }
        return ActionResultType.SUCCESS;
    }
}
