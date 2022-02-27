package studios.nightek.consume.items;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import studios.nightek.consume.ConsumerItems;
import studios.nightek.consume.accounting.AccountManager;
import studios.nightek.consume.ui.ReceiptScreen;

public class ReceiptItem extends Item {
    public ReceiptItem() {
        super(new Properties());
        setRegistryName("receipt");
    }

    public static ItemStack build(String from, int amount, ReceiptContentBuilder contents) {
        ItemStack stack = new ItemStack(ConsumerItems.RECEIPT, 1);

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("issuer", from);
        nbt.putInt("amount", amount);
        nbt.putString("contents", contents.build());
        stack.setTag(nbt);

        return stack;
    }

    public static int getReceiptItemAmount(ItemStack item) {
        CompoundNBT nbt = item.getOrCreateTag();

        return Math.abs(nbt.contains("amount") ? nbt.getInt("amount") : 0);
    }

    public static String[] getReceiptItemContents(ItemStack item) {
        CompoundNBT nbt = item.getOrCreateTag();

        return nbt.contains("contents") ? nbt.getString("contents").split(";") : new String[0];
    }

    public static String getReceiptItemContentsRaw(ItemStack item) {
        CompoundNBT nbt = item.getOrCreateTag();

        return nbt.contains("contents") ? nbt.getString("contents") : "";
    }

    public static String getReceiptItemDescription(ItemStack item) {
        CompoundNBT nbt = item.getOrCreateTag();
        String finalText = "";

        finalText += (nbt.contains("issuer") ? nbt.getString("issuer") : "(unknown)") + ";;";
        finalText += getReceiptItemContentsRaw(item) + ";";
        finalText += "Total amount due : " + (nbt.contains("amount") ? nbt.getInt("amount") : "(unknown)") + " " + AccountManager.CURRENCY_NAME + ";";

        return finalText;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (!context.getWorld().isRemote) return ActionResultType.FAIL; // Runs only on client

        Minecraft.getInstance().displayGuiScreen(new ReceiptScreen(context.getItem()));
        return ActionResultType.SUCCESS;
    }

    public static class ReceiptContentBuilder {
        String finalValue = "";

        public ReceiptContentBuilder append(String line) {
            finalValue += line.replace(',', '_') + ";";

            return this;
        }

        public String build() {
            return finalValue;
        }
    }
}
