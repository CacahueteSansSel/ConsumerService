package dev.cacahuete.consume.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import dev.cacahuete.consume.accounting.AccountAccessToken;

// This packet is sent from the client to the server to begin a crypto transaction
// targeting a cash machine tile entity (CashMachineTileEntity)
public class ClientToServerProcessPaymentPacket {
    public static final int ID = 12;

    public AccountAccessToken accessToken;
    public BlockPos tileEntityPos;
    public boolean generateReceipt;

    public ClientToServerProcessPaymentPacket() {

    }

    public ClientToServerProcessPaymentPacket(AccountAccessToken accessToken, BlockPos tileEntityPos, boolean receipt) {
        this.accessToken = accessToken;
        this.tileEntityPos = tileEntityPos;
        this.generateReceipt = receipt;
    }

    public static ClientToServerProcessPaymentPacket read(PacketBuffer buf) {
        ClientToServerProcessPaymentPacket packet = new ClientToServerProcessPaymentPacket();
        packet.accessToken = new AccountAccessToken().read(buf);
        packet.tileEntityPos = buf.readBlockPos();
        packet.generateReceipt = buf.readBoolean();
        return packet;
    }

    public void write(PacketBuffer buf) {
        accessToken.write(buf);
        buf.writeBlockPos(tileEntityPos);
        buf.writeBoolean(generateReceipt);
    }
}
