package dev.cacahuete.consume.network.packets;

import net.minecraft.network.PacketBuffer;
import dev.cacahuete.consume.accounting.AccountAccessToken;
import dev.cacahuete.consume.accounting.Account;

public class ServerToClientCryptoLoginResponsePacket {
    public static final int ID = 11;

    public Account wallet;
    public AccountAccessToken token;
    public boolean isError;
    public String message;

    public ServerToClientCryptoLoginResponsePacket() {

    }

    public ServerToClientCryptoLoginResponsePacket(AccountAccessToken token, Account wallet) {
        this.token = token;
        this.wallet = wallet;
        this.isError = false;
    }

    public ServerToClientCryptoLoginResponsePacket(String errorMessage) {
        this.token = null;
        this.isError = true;
        this.message = errorMessage;
    }

    public static ServerToClientCryptoLoginResponsePacket read(PacketBuffer buf) {
        ServerToClientCryptoLoginResponsePacket packet = new ServerToClientCryptoLoginResponsePacket();
        packet.isError = buf.readBoolean();
        if (!packet.isError) {
            packet.token = new AccountAccessToken().read(buf);
            packet.wallet = new Account().read(buf);
        } else {
            packet.message = buf.readString();
        }
        return packet;
    }

    public void write(PacketBuffer buf) {
        buf.writeBoolean(isError);
        if (isError) buf.writeString(message);
        else {
            token.write(buf);
            wallet.write(buf);
        }
    }
}
