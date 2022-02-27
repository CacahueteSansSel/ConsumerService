package studios.nightek.consume.network.packets;

import net.minecraft.network.PacketBuffer;
import studios.nightek.consume.accounting.AccountUtilities;

public class ClientToServerCryptoLoginPacket {
    public static final int ID = 10;

    public String walletId;
    public int passcode;

    public ClientToServerCryptoLoginPacket() {

    }

    public ClientToServerCryptoLoginPacket(String walletId, int passcode) {
        this.walletId = walletId;
        this.passcode = passcode;
    }

    public static ClientToServerCryptoLoginPacket read(PacketBuffer buf) {
        ClientToServerCryptoLoginPacket packet = new ClientToServerCryptoLoginPacket();
        packet.walletId = buf.readString(AccountUtilities.ACCOUNT_ID_MAX_LENGTH);
        packet.passcode = buf.readInt();
        return packet;
    }

    public void write(PacketBuffer buf) {
        buf.writeString(walletId, AccountUtilities.ACCOUNT_ID_MAX_LENGTH);
        buf.writeInt(passcode);
    }
}
