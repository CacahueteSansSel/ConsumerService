package studios.nightek.consume.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import studios.nightek.consume.accounting.AccountUtilities;

public class ClientToServerApplyPackagerPricePacket {
    public static final int ID = 15;

    public BlockPos packagerTEPos;
    public int price;

    public ClientToServerApplyPackagerPricePacket() {

    }

    public ClientToServerApplyPackagerPricePacket(BlockPos pos, int price) {
        packagerTEPos = pos;
        this.price = price;
    }

    public boolean isPriceValid() {
        if (price < 0) return false;
        if (price > 99999) return false;

        return true;
    }

    public static ClientToServerApplyPackagerPricePacket read(PacketBuffer buf) {
        ClientToServerApplyPackagerPricePacket packet = new ClientToServerApplyPackagerPricePacket();
        packet.packagerTEPos = buf.readBlockPos();
        packet.price = buf.readInt();
        return packet;
    }

    public void write(PacketBuffer buf) {
        buf.writeBlockPos(packagerTEPos);
        buf.writeInt(price);
    }
}
