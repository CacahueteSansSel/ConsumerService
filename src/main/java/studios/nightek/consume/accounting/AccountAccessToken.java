package studios.nightek.consume.accounting;

import net.minecraft.network.PacketBuffer;

import java.util.Objects;
import java.util.UUID;

public class AccountAccessToken {
    public String value;
    public String issuer;
    public int serverLifetimeTick;

    public static AccountAccessToken generate(String issuer) {
        AccountAccessToken tok = new AccountAccessToken();
        tok.value = UUID.randomUUID().toString();
        tok.issuer = issuer;
        tok.serverLifetimeTick = 120;

        return tok;
    }

    public AccountAccessToken() {

    }

    public AccountAccessToken read(PacketBuffer buf) {
        value = buf.readString();
        issuer = buf.readString();

        return this;
    }

    public void write(PacketBuffer buf) {
        buf.writeString(value);
        buf.writeString(issuer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountAccessToken that = (AccountAccessToken) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
