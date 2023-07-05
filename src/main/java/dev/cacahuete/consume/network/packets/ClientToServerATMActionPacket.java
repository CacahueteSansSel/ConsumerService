package dev.cacahuete.consume.network.packets;

import net.minecraft.network.PacketBuffer;
import dev.cacahuete.consume.accounting.AccountAccessToken;

// This packet is sent from the client to the server to process an ATM action
// like renaming a wallet, withdrawing and converting SE to emerald, etc...
public class ClientToServerATMActionPacket {
    public static final int ID = 14;

    public AccountAccessToken accessToken;
    public Action action;
    public String stringArg = "";
    public int intArg = 0;

    public ClientToServerATMActionPacket() {

    }

    public ClientToServerATMActionPacket(AccountAccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public ClientToServerATMActionPacket forDisconnecting() {
        this.action = Action.Disconnect;

        return this;
    }

    public ClientToServerATMActionPacket forRenamingWallet(String newName) {
        this.action = Action.RenameAccount;
        this.stringArg = newName;

        return this;
    }

    public ClientToServerATMActionPacket forWithdrawingWallet(int amount) {
        this.action = Action.Withdraw;
        this.intArg = amount;

        return this;
    }

    public ClientToServerATMActionPacket forDepositingWallet(int amount) {
        this.action = Action.Deposit;
        this.intArg = amount;

        return this;
    }

    public ClientToServerATMActionPacket forSendingWallet(int amount, String targetWalletId) {
        this.action = Action.Send;
        this.intArg = amount;
        this.stringArg = targetWalletId;

        return this;
    }

    public static ClientToServerATMActionPacket read(PacketBuffer buf) {
        ClientToServerATMActionPacket packet = new ClientToServerATMActionPacket();
        packet.accessToken = new AccountAccessToken().read(buf);
        packet.action = Action.forIndex(buf.readByte());
        packet.stringArg = buf.readString();
        packet.intArg = buf.readInt();
        return packet;
    }

    public void write(PacketBuffer buf) {
        accessToken.write(buf);
        buf.writeByte(action.getValue());
        buf.writeString(stringArg);
        buf.writeInt(intArg);
    }

    public enum Action {
        RenameAccount(0),
        Withdraw(1),
        Deposit(2),
        Send(3),
        Disconnect(4);

        private byte value;

        private Action(int v) {
            this.value = (byte)v;
        }

        public static Action forIndex(int v) {
            switch (v)
            {
                case 0:
                    return RenameAccount;
                case 1:
                    return Withdraw;
                case 2:
                    return Deposit;
                case 3:
                    return Send;
                case 4:
                    return Disconnect;
            }
            return null;
        }

        public byte getValue() {return value;}
    }
}
