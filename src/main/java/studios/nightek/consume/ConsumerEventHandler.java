package studios.nightek.consume;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import studios.nightek.consume.commands.BankNetCommand;
import studios.nightek.consume.accounting.AccountManager;
import studios.nightek.consume.generation.ore.ConsumerOreGeneration;
import studios.nightek.consume.generation.surface.ConsumerSurfaceGeneration;
import studios.nightek.consume.marketing.MarketingUtilities;

public class ConsumerEventHandler {
    @SubscribeEvent
    public static void onCommandRegistry(final RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
        BankNetCommand.register(commandDispatcher);
    }

    @SubscribeEvent
    public static void onBiomeLoad(final BiomeLoadingEvent event) {
        ConsumerSurfaceGeneration.generate(event);
        ConsumerOreGeneration.generate(event);
    }

    @SubscribeEvent
    public static void onWorldUnloaded(WorldEvent.Unload event) {
        AccountManager.clean();
    }
}
