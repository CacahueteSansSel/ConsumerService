package studios.nightek.consume.accounting;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import studios.nightek.consume.entities.BankAccountControllerBlockTileEntity;
import studios.nightek.consume.network.ConsumerClientHandler;
import studios.nightek.consume.network.ConsumerNetwork;
import studios.nightek.consume.network.packets.*;

import java.util.ArrayList;
import java.util.function.Function;

public class AccountManager {
    public static final String CURRENCY_NAME = "SE";
    public static final String CURRENCY_FULL_NAME = "StableEmerald";
    static ArrayList<BankAccountControllerBlockTileEntity> banknet = new ArrayList<>();

    public static boolean isBlockchainEmpty() {
        return banknet.size() == 0;
    }

    public static Account[] getAllWallets() {
        Account[] array = new Account[banknet.size()];
        int i = 0;
        for (BankAccountControllerBlockTileEntity te : banknet) {
            array[i] = te.getWallet();
            i++;
        }
        return array;
    }

    public static boolean isTokenUpToDate(AccountAccessToken token) {
        for (BankAccountControllerBlockTileEntity te : banknet) {
            if (te.getOpenedToken().value.equals(token.value)) return true;
        }

        return false;
    }

    public static BankAccountControllerBlockTileEntity getTokenSource(AccountAccessToken token) {
        for (BankAccountControllerBlockTileEntity te : banknet) {
            AccountAccessToken teTok = te.getOpenedToken();
            if (teTok != null && teTok.value.equals(token.value)) return te;
        }

        return null;
    }

    public static void clean() {
        banknet.clear();
    }

    public static void unregister(BankAccountControllerBlockTileEntity te) {
        banknet.remove(te);
    }

    public static void register(BankAccountControllerBlockTileEntity te) {
        for (BankAccountControllerBlockTileEntity t : banknet) {
            if (t.getPos() == te.getPos()) return;
        }

        banknet.add(te);
    }

    public static void clientAccessWallet(String walletId, int passcode, Function<ServerToClientCryptoLoginResponsePacket, Boolean> callback)
    {
        ConsumerClientHandler.setCryptoLoginResponseFutureHandler(callback);

        ConsumerNetwork.channel.sendToServer(new ClientToServerCryptoLoginPacket(walletId, passcode));
    }

    public static void clientProcessPaymentForTileEntity(AccountAccessToken token, BlockPos pos, boolean receipt, Function<ServerToClientPaymentResponsePacket, Boolean> callback)
    {
        ConsumerClientHandler.setPaymentResponseFutureHandler(callback);

        ConsumerNetwork.channel.sendToServer(new ClientToServerProcessPaymentPacket(token, pos, receipt));
    }

    public static AccountTransaction newTransactionFromTo(String walletFromId, int walletFromPasscode, String walletToId, float amount) {
        WalletAccessResponse resp = accessWallet(walletFromId, walletFromPasscode);
        if (resp == null) return null;

        if (resp.isWrongPasscode()) return null;
        Account toWallet = accessWalletUnsafe(walletToId);

        return AccountTransaction.run(amount, resp.getWallet().tileEntity, toWallet.tileEntity);
    }

    public static AccountTransaction newTransactionFromTo(AccountAccessToken fromToken, String walletToId, float amount) {
        BankAccountControllerBlockTileEntity te = getTokenSource(fromToken);
        if (te == null) return null;

        Account toWallet = accessWalletUnsafe(walletToId);

        return AccountTransaction.run(amount, te, toWallet.tileEntity);
    }

    public static AccountTransaction newTransactionFrom(String walletId, int walletPasscode, float amount) {
        WalletAccessResponse resp = accessWallet(walletId, walletPasscode);
        if (resp == null) return null;

        if (amount <= 0) return null;
        if (resp.isWrongPasscode()) return null;

        return AccountTransaction.run(-amount, resp.getWallet());
    }

    public static AccountTransaction newTransactionTo(String walletId, float amount) {
        Account wallet = accessWalletUnsafe(walletId);
        if (wallet == null) return null;

        if (amount <= 0) return null;

        return AccountTransaction.run(amount, wallet);
    }

    public static Account changeWalletPasscode(String id, int newPasscode) {
        for (BankAccountControllerBlockTileEntity te : banknet) {
            if (te.getWallet().id.equals(id)) {
                te.changePasscode(newPasscode);
                return te.getWallet();
            }
        }

        return null;
    }

    public static Account accessWalletUnsafe(String id) {
        for (BankAccountControllerBlockTileEntity te : banknet) {
            if (te.getWallet().id.equals(id)) return te.getWallet();
        }

        return null;
    }

    public static WalletAccessResponse accessWallet(String id, int passcode) {
        for (BankAccountControllerBlockTileEntity te : banknet) {
            Account wallet = te.getWallet();
            if (wallet == null || !wallet.id.equals(id)) continue;

            if (wallet.tryPasscode(passcode))
                return new WalletAccessResponse(wallet);
            else return new WalletAccessResponse();
        }

        return null;
    }

    public static class ATM {
        public static void disconnect(AccountAccessToken token) {

            ClientToServerATMActionPacket packet = new ClientToServerATMActionPacket(token)
                    .forDisconnecting();

            ConsumerNetwork.channel.sendToServer(packet);
        }

        public static void rename(AccountAccessToken token, String newName, Function<ServerToClientPaymentResponsePacket, Boolean> callback) {

            ConsumerClientHandler.setPaymentResponseFutureHandler(callback);
            ClientToServerATMActionPacket packet = new ClientToServerATMActionPacket(token)
                    .forRenamingWallet(newName);

            ConsumerNetwork.channel.sendToServer(packet);
        }

        public static void withdraw(AccountAccessToken token, int amount, Function<ServerToClientPaymentResponsePacket, Boolean> callback) {

            ConsumerClientHandler.setPaymentResponseFutureHandler(callback);
            ClientToServerATMActionPacket packet = new ClientToServerATMActionPacket(token)
                    .forWithdrawingWallet(amount);

            ConsumerNetwork.channel.sendToServer(packet);
        }

        public static void deposit(AccountAccessToken token, int amount, Function<ServerToClientPaymentResponsePacket, Boolean> callback) {

            ConsumerClientHandler.setPaymentResponseFutureHandler(callback);
            ClientToServerATMActionPacket packet = new ClientToServerATMActionPacket(token)
                    .forDepositingWallet(amount);

            ConsumerNetwork.channel.sendToServer(packet);
        }

        public static void send(AccountAccessToken token, String targetWalletId, int amount, Function<ServerToClientPaymentResponsePacket, Boolean> callback) {

            ConsumerClientHandler.setPaymentResponseFutureHandler(callback);
            ClientToServerATMActionPacket packet = new ClientToServerATMActionPacket(token)
                    .forSendingWallet(amount, targetWalletId);

            ConsumerNetwork.channel.sendToServer(packet);
        }
    }

    public static class WalletAccessResponse {
        boolean mem_isWrongPasscode;
        Account wallet;

        public WalletAccessResponse() {
            mem_isWrongPasscode = true;
        }

        public WalletAccessResponse(Account wallet) {
            this.wallet = wallet;
            mem_isWrongPasscode = false;
        }

        public static WalletAccessResponse read(PacketBuffer buf) {
            WalletAccessResponse resp = new WalletAccessResponse();
            byte code = buf.readByte();
            if (code == 0) {
                resp.mem_isWrongPasscode = false;
                Account wallet = new Account().read(buf);
                resp.wallet = wallet;
                return resp;
            } else if (code == 1) {
                resp.mem_isWrongPasscode = true;
                return resp;
            } else {
                return null;
            }
        }

        public static void write(WalletAccessResponse resp, PacketBuffer buf) {
            if (resp == null) {
                buf.writeByte(2);
                return;
            } else if (resp.isWrongPasscode()) {
                buf.writeByte(1);
                return;
            } else {
                buf.writeByte(0);
                resp.wallet.write(buf);
            }
        }

        public Account getWallet() {
            return wallet;
        }

        public boolean isWrongPasscode() {
            return mem_isWrongPasscode;
        }

        public boolean isWalletAccessible() {
            return !isWrongPasscode() && wallet != null && !wallet.tileEntity.isLocked();
        }
    }
}
