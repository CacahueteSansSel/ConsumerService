package studios.nightek.consume.network.packets;

import net.minecraft.network.PacketBuffer;
import studios.nightek.consume.entities.BankAccountControllerBlockTileEntity;

public class ServerToClientPaymentResponsePacket {
    public static final int ID = 13;

    public boolean isError;
    public String message;
    public String walletName;
    public float amount;
    public String issuer;

    public ServerToClientPaymentResponsePacket() {

    }

    public ServerToClientPaymentResponsePacket(BankAccountControllerBlockTileEntity.TransactionResponse response, float amount, String issuer, String walletName) {
        this.amount = amount;
        this.issuer = issuer;
        this.walletName = walletName;

        switch (response) {
            case Success:
                message = "";
                isError = false;
                break;
            case Refused:
                message = "Transaction refused";
                isError = true;
                break;
            case InvalidWallet:
                message = "Invalid wallet";
                isError = true;
            case Locked:
                message = "Wallet locked from redstone";
                isError = true;
        }
    }

    public ServerToClientPaymentResponsePacket(boolean isError, String message, float amount, String issuer, String walletName) {
        this.message = message;
        this.isError = isError;
        this.amount = amount;
        this.issuer = issuer;
        this.walletName = walletName;
    }

    public static ServerToClientPaymentResponsePacket read(PacketBuffer buf) {
        ServerToClientPaymentResponsePacket packet = new ServerToClientPaymentResponsePacket();
        packet.isError = buf.readBoolean();
        packet.message = buf.readString();
        packet.amount = buf.readFloat();
        packet.issuer = buf.readString();
        return packet;
    }

    public void write(PacketBuffer buf) {
        buf.writeBoolean(isError);
        buf.writeString(message);
        buf.writeFloat(amount);
        buf.writeString(issuer == null ? "Unknown" : issuer);
    }
}
