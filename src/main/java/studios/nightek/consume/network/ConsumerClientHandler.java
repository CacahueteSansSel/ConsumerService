package studios.nightek.consume.network;

import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;
import studios.nightek.consume.network.packets.ServerToClientCryptoLoginResponsePacket;
import studios.nightek.consume.network.packets.ServerToClientPaymentResponsePacket;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConsumerClientHandler {
    static Function<ServerToClientCryptoLoginResponsePacket, Boolean> cryptoLoginResponseFutureHandler;
    static Function<ServerToClientPaymentResponsePacket, Boolean> paymentResponseFutureHandler;
    public static boolean complies(String version) {
        return version.equals(ConsumerNetwork.VERSION);
    }

    public static void setCryptoLoginResponseFutureHandler(Function<ServerToClientCryptoLoginResponsePacket, Boolean> handler) {
        cryptoLoginResponseFutureHandler = handler;
    }

    public static void setPaymentResponseFutureHandler(Function<ServerToClientPaymentResponsePacket, Boolean> handler) {
        paymentResponseFutureHandler = handler;
    }

    public static void onCryptoLoginResponsePacketReceived(final ServerToClientCryptoLoginResponsePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);

        if (sideReceived.isServer()) {
            System.out.println("Received packet on server !");
            return;
        }

        Optional<ClientWorld> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
        if (!clientWorld.isPresent()) {
            System.out.println("TargetEffectMessageToClient context could not provide a ClientWorld.");
            return;
        }

        // This code creates a new task which will be executed by the client during the next tick
        //  In this case, the task is to call messageHandlerOnClient.processMessage(worldclient, message)
        ctx.enqueueWork(() -> process(clientWorld.get(), packet));
    }

    public static void onPaymentResponsePacketReceived(final ServerToClientPaymentResponsePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);

        if (sideReceived.isServer()) {
            System.out.println("Received packet on server !");
            return;
        }

        Optional<ClientWorld> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
        if (!clientWorld.isPresent()) {
            System.out.println("TargetEffectMessageToClient context could not provide a ClientWorld.");
            return;
        }

        // This code creates a new task which will be executed by the client during the next tick
        //  In this case, the task is to call messageHandlerOnClient.processMessage(worldclient, message)
        ctx.enqueueWork(() -> process(clientWorld.get(), packet));
    }

    static void process(ClientWorld world, ServerToClientCryptoLoginResponsePacket packet) {
        if (cryptoLoginResponseFutureHandler != null) {
            cryptoLoginResponseFutureHandler.apply(packet);
            cryptoLoginResponseFutureHandler = null;
        } else {
            System.out.println("Future handler is null !");
        }
    }

    static void process(ClientWorld world, ServerToClientPaymentResponsePacket packet) {
        if (paymentResponseFutureHandler != null) {
            paymentResponseFutureHandler.apply(packet);
            paymentResponseFutureHandler = null;
        } else {
            System.out.println("Future handler is null !");
        }
    }
}
