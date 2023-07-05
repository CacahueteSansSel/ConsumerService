package dev.cacahuete.consume;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import dev.cacahuete.consume.commands.BankNetCommand;
import dev.cacahuete.consume.accounting.AccountManager;
import dev.cacahuete.consume.generation.ore.ConsumerOreGeneration;
import dev.cacahuete.consume.generation.surface.ConsumerSurfaceGeneration;

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
