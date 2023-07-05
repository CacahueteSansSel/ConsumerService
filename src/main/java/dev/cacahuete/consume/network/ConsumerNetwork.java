package dev.cacahuete.consume.network;

import dev.cacahuete.consume.network.packets.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ConsumerNetwork {
    public static final String VERSION = "2.2-STABLE";
    public static SimpleChannel channel;
    static ResourceLocation loc = new ResourceLocation("consume", "network");

    public static void init() {
        channel = NetworkRegistry.newSimpleChannel(loc,
                () -> VERSION,
                ConsumerClientHandler::complies,
                ConsumerServerHandler::complies);

        channel.registerMessage(ClientToServerCryptoLoginPacket.ID, ClientToServerCryptoLoginPacket.class, ClientToServerCryptoLoginPacket::write, ClientToServerCryptoLoginPacket::read, ConsumerServerHandler::onCryptoLoginPacketReceived);
        channel.registerMessage(ClientToServerProcessPaymentPacket.ID, ClientToServerProcessPaymentPacket.class, ClientToServerProcessPaymentPacket::write, ClientToServerProcessPaymentPacket::read, ConsumerServerHandler::onProcessPaymentPacketReceived);
        channel.registerMessage(ClientToServerATMActionPacket.ID, ClientToServerATMActionPacket.class, ClientToServerATMActionPacket::write, ClientToServerATMActionPacket::read, ConsumerServerHandler::onATMActionPacketReceived);
        channel.registerMessage(ClientToServerApplyPackagerPricePacket.ID, ClientToServerApplyPackagerPricePacket.class, ClientToServerApplyPackagerPricePacket::write, ClientToServerApplyPackagerPricePacket::read, ConsumerServerHandler::onApplyPackagerPricePacketReceived);

        channel.registerMessage(ServerToClientCryptoLoginResponsePacket.ID, ServerToClientCryptoLoginResponsePacket.class, ServerToClientCryptoLoginResponsePacket::write, ServerToClientCryptoLoginResponsePacket::read, ConsumerClientHandler::onCryptoLoginResponsePacketReceived);
        channel.registerMessage(ServerToClientPaymentResponsePacket.ID, ServerToClientPaymentResponsePacket.class, ServerToClientPaymentResponsePacket::write, ServerToClientPaymentResponsePacket::read, ConsumerClientHandler::onPaymentResponsePacketReceived);
    }
}
