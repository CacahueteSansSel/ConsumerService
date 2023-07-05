package dev.cacahuete.consume.accounting;

import dev.cacahuete.consume.entities.BankAccountControllerBlockTileEntity;

public class AccountTransaction {
    public String id;
    public float amount;
    public String walletSourceId;
    public String walletTargetId;
    public BankAccountControllerBlockTileEntity.TransactionResponse response;

    public static AccountTransaction run(float amount, BankAccountControllerBlockTileEntity source, BankAccountControllerBlockTileEntity target) {
        BankAccountControllerBlockTileEntity.TransactionResponse resp = source.beginTransaction(target, amount);
        return new AccountTransaction(AccountUtilities.generateTransactionId(), amount, source.getWallet().id, target.getWallet().id, resp);
    }

    public static AccountTransaction run(float amount, Account target) {
        BankAccountControllerBlockTileEntity tgtTileEntity = target.tileEntity;

        BankAccountControllerBlockTileEntity.TransactionResponse resp = tgtTileEntity.beginVoidTransaction(amount);
        return new AccountTransaction(AccountUtilities.generateTransactionId(), amount, "void", target.id, resp);
    }

    public AccountTransaction(String id, float amount, String walletSrcId, String walletTgtId) {
        this.id = id;
        this.amount = amount;
        response = BankAccountControllerBlockTileEntity.TransactionResponse.Unknown;
        walletSourceId = walletSrcId;
        walletTargetId = walletTgtId;
    }

    public AccountTransaction(String id, float amount, String walletSrcId, String walletTgtId, BankAccountControllerBlockTileEntity.TransactionResponse response) {
        this.id = id;
        this.amount = amount;
        this.response = response;
        walletSourceId = walletSrcId;
        walletTargetId = walletTgtId;
    }
}
