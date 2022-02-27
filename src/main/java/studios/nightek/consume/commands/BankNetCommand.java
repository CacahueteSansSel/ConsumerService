package studios.nightek.consume.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import studios.nightek.consume.accounting.AccountManager;
import studios.nightek.consume.accounting.AccountTransaction;
import studios.nightek.consume.accounting.Account;

public class BankNetCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> cryptoCommand
                = Commands.literal("banknet")
                .requires((commandSource -> commandSource.hasPermissionLevel(2)))
                .then(registerGive(Commands.literal("give")))
                .then(registerSetCode(Commands.literal("setcode")))
                .then(Commands.literal("accounts").executes(BankNetCommand::executeCryptoWallets))
                .then(Commands.literal("query").then(Commands.argument("account_id", StringArgumentType.word()).executes(BankNetCommand::executeCryptoQuery)));

        dispatcher.register(cryptoCommand);
    }

    static LiteralArgumentBuilder<CommandSource> registerSetCode(LiteralArgumentBuilder<CommandSource> bld) {
        return bld.then(
                Commands.argument("account_id", StringArgumentType.word())
                        .then(Commands.argument("code", IntegerArgumentType.integer(0, 9999))
                                .executes(BankNetCommand::executeCryptoSetCode)));
    }

    static LiteralArgumentBuilder<CommandSource> registerGive(LiteralArgumentBuilder<CommandSource> bld) {
        return bld.then(
                Commands.argument("account_id", StringArgumentType.word())
                        .then(Commands.argument("amount", FloatArgumentType.floatArg(0))
                                .executes(BankNetCommand::executeCryptoGive)));
    }

    static int executeCryptoWallets(CommandContext<CommandSource> ctx) {
        ctx.getSource().sendFeedback(new StringTextComponent("List of available bank accounts :"), true);
        int id = 0;
        for (Account wallet : AccountManager.getAllWallets()) {
            if (wallet.id.length() != 16) continue;
            String finalStr = TextFormatting.BLUE.toString() + "#" + id + TextFormatting.RESET.toString() + " : " + wallet.getDisplayName();

            ITextComponent clickableID = TextComponentUtils.wrapWithSquareBrackets((new StringTextComponent(wallet.id)).modifyStyle((p_211752_2_) -> {
                return p_211752_2_.setFormatting(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, wallet.id)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.copy.click"))).setInsertion(wallet.id);
            }));

            ctx.getSource().sendFeedback(new StringTextComponent(finalStr), true);
            ctx.getSource().sendFeedback(new StringTextComponent("Account ID : "), true);
            ctx.getSource().sendFeedback(clickableID, true);
            id++;
        }
        if (id == 0) {
            ctx.getSource().sendFeedback(new StringTextComponent("No open account available"), true);
        }
        return 0;
    }

    static int executeCryptoGive(CommandContext<CommandSource> ctx) {
        String walletId = StringArgumentType.getString(ctx, "account_id");
        float amount = FloatArgumentType.getFloat(ctx, "amount");

        AccountTransaction transaction = AccountManager.newTransactionTo(walletId, amount);
        switch (transaction.response) {
            case Success:
                ctx.getSource().sendFeedback(new StringTextComponent(TextFormatting.GREEN.toString() + "Transaction of " + transaction.amount + " " + AccountManager.CURRENCY_NAME + " of cryptocurrency has succeeded ! Transaction id : " + transaction.id), true);
                break;
            case Refused:
                ctx.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED.toString() + "Transaction of " + transaction.amount + " " + AccountManager.CURRENCY_NAME + " has been refused ! Transaction id : " + transaction.id), true);
                return 1;
            case InvalidWallet:
                ctx.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED.toString() + "Transaction of " + transaction.amount + " " + AccountManager.CURRENCY_NAME + " cannot succeed because one of the specified wallets is invalid or unvalidated ! Transaction id : " + transaction.id), true);
                return 1;
        }
        return 0;
    }

    static int executeCryptoSetCode(CommandContext<CommandSource> ctx) {
        String walletId = StringArgumentType.getString(ctx, "account_id");
        int code = IntegerArgumentType.getInteger(ctx, "code");

        boolean success = AccountManager.changeWalletPasscode(walletId, code) == null;
        if (success) {
            ctx.getSource().sendFeedback(new TranslationTextComponent("ui.crypto.codechanged"), true);
        } else {
            ctx.getSource().sendFeedback(new TranslationTextComponent("ui.crypto.codechangederror"), true);
        }
        return success ? 1 : 0;
    }

    static int executeCryptoQuery(CommandContext<CommandSource> ctx) {
        String walletId = StringArgumentType.getString(ctx, "account_id");

        Account wallet = AccountManager.accessWalletUnsafe(walletId);
        ctx.getSource().sendFeedback(new StringTextComponent(TextFormatting.GOLD.toString() + "Bank account details : "), true);
        ctx.getSource().sendFeedback(new StringTextComponent(TextFormatting.RESET.toString() + "Verified : " + (wallet.verified ? "true" : "false")), true);
        ctx.getSource().sendFeedback(new StringTextComponent(TextFormatting.RESET.toString() + "Amount : " + (wallet.isBankrupt() ? TextFormatting.RED : TextFormatting.GREEN).toString() + wallet.amount + " " + AccountManager.CURRENCY_NAME + TextFormatting.RESET), true);
        return 0;
    }
}
