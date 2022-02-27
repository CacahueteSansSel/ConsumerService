package studios.nightek.consume;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import studios.nightek.consume.accounting.AccountManager;
import studios.nightek.consume.marketing.MarketingUtilities;

public class ConsumerClientEventHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!MarketingUtilities.isCommerceItem(event.getItemStack())) return;
        if (MarketingUtilities.isItemBought(event.getItemStack())) {
            event.getToolTip().add(new TranslationTextComponent("ui.consume.bought"));
            return;
        }

        int price = MarketingUtilities.getItemPrice(event.getItemStack());
        event.getToolTip().add(new StringTextComponent(TextFormatting.GREEN.toString() + price + " " + AccountManager.CURRENCY_NAME));
    }
}
