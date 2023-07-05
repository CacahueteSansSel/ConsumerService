package dev.cacahuete.consume.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import dev.cacahuete.consume.ui.CreditCardScreen;

import javax.annotation.Nullable;
import java.util.List;

public class CreditCardItem extends Item {
    public CreditCardItem() {
        super(new Properties().maxStackSize(1).isImmuneToFire());
        setRegistryName("crypto_card");
    }

    public static String getCardWalletId(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        return nbt.contains("wallet_id") ? nbt.getString("wallet_id").trim() : null;
    }

    public static String getCardDisplayName(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        return nbt.contains("wallet_name") ? nbt.getString("wallet_name") : "Unnamed";
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        String walletId = getCardWalletId(stack);
        if (getCardDisplayName(stack) != null) {
            tooltip.add(new StringTextComponent(getCardDisplayName(stack)));
        }
        if (walletId == null) {
            tooltip.add(new TranslationTextComponent("item.consume.crypto_card.nowallet"));
        } else {
            tooltip.add(new StringTextComponent(TextFormatting.GRAY.toString() + walletId));
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (!context.getWorld().isRemote) return ActionResultType.PASS;

        if (context.getPlayer().isSneaking()) {
            String id = getCardWalletId(context.getItem());
            Minecraft.getInstance().keyboardListener.setClipboardString(id);
            context.getPlayer().sendStatusMessage(new TranslationTextComponent("item.consume.crypto_card.copied"), true);
        } else {
            Minecraft.getInstance().displayGuiScreen(new CreditCardScreen(context.getItem()));
        }

        return ActionResultType.SUCCESS;
    }
}
