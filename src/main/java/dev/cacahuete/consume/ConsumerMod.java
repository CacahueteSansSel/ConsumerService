package dev.cacahuete.consume;

import dev.cacahuete.consume.network.ConsumerNetwork;
import dev.cacahuete.consume.ui.*;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dev.cacahuete.consume.entities.ProtectiveShelfTileEntity;
import dev.cacahuete.consume.entities.ProtectiveShelfTileEntityRenderer;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("consume")
public class ConsumerMod
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public ConsumerMod() {
        ConsumerClientEventHandler clientEventHandler = new ConsumerClientEventHandler();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSideStartup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ConsumerEventHandler.class);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // Register ourselves for server and other game events we are interested in

        ConsumerNetwork.init();
    }

    private void clientSideStartup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(ConsumerClientEventHandler.class);
        ScreenManager.registerFactory((ContainerType<ProtectiveShelfContainer>)ConsumerContainers.ContainerProtectiveShelf, ProtectiveShelfContainerScreen::new);
        ScreenManager.registerFactory((ContainerType<CashMachineContainer>)ConsumerContainers.ContainerCashMachine, CashMachineContainerScreen::new);
        ScreenManager.registerFactory((ContainerType<PackagerContainer>)ConsumerContainers.ContainerPackager, PackagerContainerScreen::new);

        ClientRegistry.bindTileEntityRenderer((TileEntityType<ProtectiveShelfTileEntity>) ConsumerTileEntities.PROTECTIVE_SHELF_TILE_ENTITY, (tileEntityRendererDispatcher -> new ProtectiveShelfTileEntityRenderer(tileEntityRendererDispatcher)));

        RenderTypeLookup.setRenderLayer(ConsumerBlocks.PROTECTIVE_SHOWCASE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ConsumerBlocks.PROTECTIVE_ENTRANCE_DOOR_BLOCK, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ConsumerBlocks.PROTECTIVE_GLASS, RenderType.getCutout());
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> evt) {
            ConsumerBlocks.register(evt);
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> evt) {
            ConsumerItems.register(evt);
        }

        @SubscribeEvent
        public static void onTERegistry(final RegistryEvent.Register<TileEntityType<?>> evt) {
            ConsumerTileEntities.register(evt);
        }

        @SubscribeEvent
        public static void onUIRegistry(final RegistryEvent.Register<ContainerType<?>> evt) {
            ConsumerContainers.init();
            ConsumerContainers.register(evt);
        }

        @SubscribeEvent
        public static void onSERegistry(final RegistryEvent.Register<SoundEvent> evt) {
            ConsumerSounds.register(evt);
        }

    }
}
