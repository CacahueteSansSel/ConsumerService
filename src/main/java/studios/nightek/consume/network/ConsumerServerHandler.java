package studios.nightek.consume.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import studios.nightek.consume.accounting.AccountManager;
import studios.nightek.consume.accounting.AccountAccessToken;
import studios.nightek.consume.accounting.AccountTransaction;
import studios.nightek.consume.accounting.Account;
import studios.nightek.consume.entities.BankAccountControllerBlockTileEntity;
import studios.nightek.consume.entities.CashMachineTileEntity;
import studios.nightek.consume.entities.PackagerTileEntity;
import studios.nightek.consume.items.ReceiptItem;
import studios.nightek.consume.marketing.MarketingUtilities;
import studios.nightek.consume.network.packets.*;

import java.util.function.Supplier;

public class ConsumerServerHandler {
    public static boolean complies(String version) {
        return version.equals(ConsumerNetwork.VERSION);
    }

    public static void onProcessPaymentPacketReceived(final ClientToServerProcessPaymentPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);

        if (!sideReceived.isServer()) {
            System.out.println("Received packet on client !");
            return;
        }

        final ServerPlayerEntity sendingPlayer = ctx.getSender();

        // This code creates a new task which will be executed by the server during the next tick,
        //  In this case, the task is to call messageHandlerOnServer.processMessage(message, sendingPlayer)
        ctx.enqueueWork(() -> process(packet, sendingPlayer));
    }

    public static void onCryptoLoginPacketReceived(final ClientToServerCryptoLoginPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);

        if (!sideReceived.isServer()) {
            System.out.println("Received packet on client !");
            return;
        }

        final ServerPlayerEntity sendingPlayer = ctx.getSender();

        // This code creates a new task which will be executed by the server during the next tick,
        //  In this case, the task is to call messageHandlerOnServer.processMessage(message, sendingPlayer)
        ctx.enqueueWork(() -> process(packet, sendingPlayer));
    }

    public static void onATMActionPacketReceived(final ClientToServerATMActionPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);

        if (!sideReceived.isServer()) {
            System.out.println("Received packet on client !");
            return;
        }

        final ServerPlayerEntity sendingPlayer = ctx.getSender();

        // This code creates a new task which will be executed by the server during the next tick,
        //  In this case, the task is to call messageHandlerOnServer.processMessage(message, sendingPlayer)
        ctx.enqueueWork(() -> process(packet, sendingPlayer));
    }

    public static void onApplyPackagerPricePacketReceived(ClientToServerApplyPackagerPricePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);

        if (!sideReceived.isServer()) {
            System.out.println("Received packet on client !");
            return;
        }

        final ServerPlayerEntity sendingPlayer = ctx.getSender();

        // This code creates a new task which will be executed by the server during the next tick,
        //  In this case, the task is to call messageHandlerOnServer.processMessage(message, sendingPlayer)
        ctx.enqueueWork(() -> process(packet, sendingPlayer));
    }

    static void process(ClientToServerProcessPaymentPacket packet, ServerPlayerEntity sender) {
        BankAccountControllerBlockTileEntity block = AccountManager.getTokenSource(packet.accessToken);
        if (block == null) {
            // Token has expired or is invalid
            // Send error message
            ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "Session token expired", -1, "Pipeline", "Unknown"));
            return;
        }
        Account wallet = block.getWallet();
        if (!wallet.verified) {
            // Wallet is not verified
            // Send error message
            ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "Invalid wallet", -1, "Pipeline", wallet.getDisplayName()));
            return;
        }
        if (wallet.isBankrupt()) {
            // Wallet is in bankrupt
            // Send error message
            ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "Bankrupt", -1, "Pipeline", wallet.getDisplayName()));
            return;
        }
        TileEntity te = sender.world.getTileEntity(packet.tileEntityPos);
        if (te == null) return;

        if (te instanceof CashMachineTileEntity) {
            CashMachineTileEntity machine = (CashMachineTileEntity)te;
            ReceiptItem.ReceiptContentBuilder builder = machine.buildReceiptContents();

            int amountToPay = machine.getTotalPriceInEmeralds();
            if (amountToPay == 0) return;
            BankAccountControllerBlockTileEntity.TransactionResponse resp = block.beginVoidTransaction(-amountToPay);

            if (resp == BankAccountControllerBlockTileEntity.TransactionResponse.Success)
            {
                if (machine.targetAccountId != null) {
                    Account accountTarget = AccountManager.accessWalletUnsafe(machine.targetAccountId);
                    if (accountTarget == null) {
                        // The target account associated with the cash machine
                        // is not found
                        ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "Target account offline", amountToPay, "Pipeline", wallet.getDisplayName()));
                        return;
                    }
                    BankAccountControllerBlockTileEntity targetBlock = accountTarget.tileEntity;
                    BankAccountControllerBlockTileEntity.TransactionResponse targetResp = targetBlock.beginVoidTransaction(amountToPay);

                    if (targetResp != BankAccountControllerBlockTileEntity.TransactionResponse.Success) {
                        // An error occurred with the target account
                        ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "A problem occurred with the target account", amountToPay, "Pipeline", wallet.getDisplayName()));
                        return;
                    }
                }

                machine.markItemsAsBought();
            }

            // Send raw response from tile entity
            ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(resp, amountToPay, machine.displayName, wallet.getDisplayName()));
            block.clearToken();

            if (packet.generateReceipt) sender.inventory.addItemStackToInventory(ReceiptItem.build(machine.displayName == null ? "SELF-CHECKOUT MACHINE" : machine.displayName, -amountToPay, builder));
        }
    }

    static void process(ClientToServerCryptoLoginPacket packet, ServerPlayerEntity sender) {
        AccountManager.WalletAccessResponse resp = AccountManager.accessWallet(packet.walletId, packet.passcode);
        if (resp == null) {
            ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientCryptoLoginResponsePacket("Invalid wallet id : the matching wallet was not found"));
        } else if (resp.isWalletAccessible()) {
            if (resp.getWallet().tileEntity.isLocked()) {
                ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientCryptoLoginResponsePacket("Your wallet is locked from redstone"));
                return;
            }
            AccountAccessToken token = resp.getWallet().tileEntity.openToken();
            if (token == null) {
                // Another token has been opened before, so we refuse anything
                ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientCryptoLoginResponsePacket("An access token has already been opened"));
            } else {
                // A new token has been opened
                ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientCryptoLoginResponsePacket(token, resp.getWallet()));
            }
        } else {
            if (resp.isWrongPasscode()) {
                ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientCryptoLoginResponsePacket("Incorrect passcode"));
            } else {
                ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientCryptoLoginResponsePacket("Unknown error"));
            }
        }
    }

    static void process(ClientToServerATMActionPacket packet, ServerPlayerEntity sender) {
        switch (packet.action) {
            case Disconnect:
            {
                BankAccountControllerBlockTileEntity te = AccountManager.getTokenSource(packet.accessToken);
                if (te == null) {
                    return;
                }
                te.clearToken();
                break;
            }
            case Deposit:
            {
                BankAccountControllerBlockTileEntity te = AccountManager.getTokenSource(packet.accessToken);
                if (te == null) {
                    ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "Invalid wallet or expired token", -1, "ATM", "Unknown"));
                    return;
                }
                if (packet.intArg <= 0) return;

                Account w = te.getWallet();
                if (!w.verified) return;

                boolean success = MarketingUtilities.consumeCurrency(sender.inventory, packet.intArg);
                if (!success) {
                    ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "Not enough emeralds", packet.intArg, "ATM", w.getDisplayName()));
                    break;
                }

                BankAccountControllerBlockTileEntity.TransactionResponse resp = te.beginVoidTransaction(packet.intArg);
                ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(resp, packet.intArg, "ATM", w.getDisplayName()));
                break;
            }
            case Withdraw:
            {
                BankAccountControllerBlockTileEntity te = AccountManager.getTokenSource(packet.accessToken);
                if (te == null) {
                    ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "Invalid wallet or expired token", -1, "ATM", "Unknown"));
                    return;
                }
                Account w = te.getWallet();
                if (!w.verified) return;
                if (packet.intArg <= 0) return;
                if (packet.intArg > 64) {
                    ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "You can't withdraw more than 64 " + AccountManager.CURRENCY_NAME, -1, "ATM", "Unknown"));
                    return;
                }
                if (w.amount < packet.intArg) {
                    ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "Issued amount is too big", packet.intArg, "ATM", w.getDisplayName()));
                    return;
                }

                BankAccountControllerBlockTileEntity.TransactionResponse resp = te.beginVoidTransaction(-packet.intArg);
                if (resp != BankAccountControllerBlockTileEntity.TransactionResponse.Success) {
                    ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(resp, packet.intArg, "ATM", w.getDisplayName()));
                    return;
                }

                int curAmount = packet.intArg;
                for (int i = 0; i < curAmount; i++)
                    sender.inventory.addItemStackToInventory(new ItemStack(Items.EMERALD, 1));

                ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(false, "", packet.intArg, "ATM", w.getDisplayName()));
                break;
            }
            case RenameAccount:
            {
                BankAccountControllerBlockTileEntity te = AccountManager.getTokenSource(packet.accessToken);
                if (te == null) {
                    ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "Invalid wallet or expired token", -1, "ATM", "Unknown"));
                    return;
                }
                te.setWalletDisplayName(packet.stringArg);
                ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(false, "Wallet renamed", -1, "ATM", te.getWallet().getDisplayName()));
                break;
            }
            case Send:
            {
                BankAccountControllerBlockTileEntity te2 = AccountManager.getTokenSource(packet.accessToken);
                if (te2 == null) {
                    ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "Invalid wallet or expired token", -1, "ATM", "Unknown"));
                    return;
                }

                int amount = packet.intArg;
                if (amount <= 0) {
                    ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(true, "Zero or negative amount is forbidden", -1, "ATM", "Unknown"));
                    return;
                }
                String targetWalletId = packet.stringArg;

                AccountTransaction tr = AccountManager.newTransactionFromTo(packet.accessToken, targetWalletId, amount);
                ConsumerNetwork.channel.send(PacketDistributor.PLAYER.with(() -> sender), new ServerToClientPaymentResponsePacket(tr.response, amount, "(Wallet) " + te2.getWallet().getDisplayName(), te2.getWallet().getDisplayName()));
                break;
            }
        }
    }

    static void process(ClientToServerApplyPackagerPricePacket packet, ServerPlayerEntity sender) {
        World world = sender.world;

        if (!packet.isPriceValid()) return;

        TileEntity te = world.getTileEntity(packet.packagerTEPos);
        if (te == null) return;

        if (te instanceof PackagerTileEntity) {
            PackagerTileEntity packagerTe = (PackagerTileEntity) te;
            packagerTe.setPriceOfContainedItem(packet.price);
        }
    }
}
