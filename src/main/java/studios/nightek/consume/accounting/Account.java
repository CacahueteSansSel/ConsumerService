package studios.nightek.consume.accounting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import studios.nightek.consume.ConsumerItems;
import studios.nightek.consume.entities.BankAccountControllerBlockTileEntity;

public class Account {
    long hash;
    long passcodeHash;
    public BankAccountControllerBlockTileEntity tileEntity;
    public float amount;
    public String id;
    public String name;
    public boolean verified;

    public Account() {
        verified = false;
    }

    public Account(BankAccountControllerBlockTileEntity tileEntity) {
        this.tileEntity = tileEntity;
        this.id = AccountUtilities.generateWalletId();
    }

    public Account(BankAccountControllerBlockTileEntity tileEntity, String id) {
        this.tileEntity = tileEntity;
        this.id = id;
        amount = 0f;
    }

    public ItemStack newCryptoCard() {
        ItemStack stack = new ItemStack(ConsumerItems.CREDIT_CARD, 1);
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("wallet_id", id);
        if (name != null) nbt.putString("wallet_name", name);
        stack.setTag(nbt);

        return stack;
    }

    public String getDisplayName() {
        return name == null ? "Unnamed Account" : name;
    }

    public long getPasscodeHash() {
        return passcodeHash;
    }

    public String getAmountDisplayable() {
        return amount + " " + AccountManager.CURRENCY_FULL_NAME;
    }

    public boolean isBankrupt() {
        return amount <= 0;
    }

    public static String preparePasscode(int passcode) {
        String passcodeStr = String.valueOf(passcode);
        if (passcodeStr.length() < 4) {
            String pad = "";
            for (int i = 0; i < (passcodeStr.length() - 4); i++)
                pad += "0";
            passcodeStr += pad;
        }
        return passcodeStr;
    }

    public void setPasscode(int passcode) {
        String passcodeStr = preparePasscode(passcode);

        passcodeHash = AccountUtilities.superBadHash(passcodeStr);
    }

    public boolean tryPasscode(int passcode) {
        long providedPasscodeHash = AccountUtilities.superBadHash(preparePasscode(passcode));

        return providedPasscodeHash == passcodeHash;
    }

    public boolean remove(float amount) {
        if (!verified || isBankrupt() || amount <= 0) return false;

        this.amount -= amount;
        tileEntity.markDirty();
        return true;
    }

    public boolean add(float amount) {
        if (!verified || amount <= 0) return false;

        this.amount += amount;
        tileEntity.markDirty();
        return true;
    }

    void calculateHash() {
        hash = AccountUtilities.superBadHash(id + String.valueOf(amount));
    }

    public void write(CompoundNBT nbt) {
        calculateHash();

        nbt.putFloat("amount", amount);
        nbt.putString("id", id);
        nbt.putLong("hash", hash);
        nbt.putLong("passcode_hash", passcodeHash);
        nbt.putBoolean("migrated", true);
        if (name != null) {
            nbt.putString("name", name);
        }
    }

    public void write(PacketBuffer buf) {
        calculateHash();

        buf.writeFloat(amount);
        buf.writeString(id, AccountUtilities.ACCOUNT_ID_MAX_LENGTH);
        buf.writeLong(hash);
        if (name != null) {
            buf.writeBoolean(true);
            buf.writeString(name);
        } else {
            buf.writeBoolean(false);
        }
        // The passcode hash is not sync for security reasons
        //buf.writeLong(passcodeHash);
    }

    public Account read(CompoundNBT nbt) {
        amount = nbt.getFloat("amount");
        id = nbt.getString("id");
        long savedHash = nbt.getLong("hash");
        passcodeHash = nbt.getLong("passcode_hash");
        if (nbt.contains("name")) name = nbt.getString("name");
        calculateHash();

        verified = savedHash == hash;
        if (!nbt.contains("migrated")) migrate();
        return this;
    }

    private void migrate() {
        id = AccountUtilities.generateWalletId();
    }

    public Account read(PacketBuffer buf) {
        amount = buf.readFloat();
        id = buf.readString(AccountUtilities.ACCOUNT_ID_MAX_LENGTH);
        long savedHash = buf.readLong();
        if (buf.readBoolean()) {
            name = buf.readString();
        }
        // The passcode hash is not sync for security reasons
        //passcodeHash = buf.readLong();
        calculateHash();

        verified = savedHash == hash;
        return this;
    }
}