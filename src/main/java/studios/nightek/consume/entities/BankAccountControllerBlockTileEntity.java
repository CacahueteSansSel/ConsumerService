package studios.nightek.consume.entities;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import studios.nightek.consume.ConsumerTileEntities;
import studios.nightek.consume.accounting.AccountManager;
import studios.nightek.consume.accounting.AccountAccessToken;
import studios.nightek.consume.accounting.Account;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BankAccountControllerBlockTileEntity extends TileEntity implements IForgeTileEntity, ITickableTileEntity {
    Account wallet;
    AccountAccessToken openedToken;
    boolean locked;

    public BankAccountControllerBlockTileEntity() {
        super(ConsumerTileEntities.BLOCKCHAIN_TILE_ENTITY);
    }

    public Account getWallet() {
        return wallet;
    }

    public void changePasscode(int newPasscode) {
        wallet.setPasscode(newPasscode);
    }

    public void setWalletDisplayName(String name) {
        wallet.name = name;
        markDirty();
    }

    public Account prepareWalletIfNeeded() {
        if (wallet != null) return wallet;

        wallet = new Account(this);
        wallet.verified = true;
        markDirty();
        return wallet;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        markDirty();
    }

    public AccountAccessToken getOpenedToken() {
        return openedToken;
    }

    public AccountAccessToken openToken() {
        if (openedToken != null) return null;
        openedToken = AccountAccessToken.generate("Block at " + this.pos.getCoordinatesAsString());

        return openedToken;
    }

    public void clearToken() {
        openedToken = null;
    }

    public TransactionResponse beginTransaction(BankAccountControllerBlockTileEntity targetWalletTileEntity, float amount) {
        if (locked) return TransactionResponse.Locked;

        if (!wallet.verified || !targetWalletTileEntity.wallet.verified)
            return TransactionResponse.InvalidWallet;

        if (!wallet.remove(amount))
            return TransactionResponse.Refused;

        if (!targetWalletTileEntity.wallet.add(amount))
            return TransactionResponse.Refused;

        return TransactionResponse.Success;
    }

    public TransactionResponse beginVoidTransaction(float amount) {
        if (locked) return TransactionResponse.Locked;
        if (!wallet.verified)
            return TransactionResponse.InvalidWallet;

        if (amount < 0) {
            if (!wallet.remove(-amount))
                return TransactionResponse.Refused;
        } else if (amount > 0) {
            if (!wallet.add(amount))
                return TransactionResponse.Refused;
        }

        return TransactionResponse.Success;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = new CompoundNBT();
        wallet.write(nbt);
        compound.put("wallet", nbt);
        compound.putBoolean("locked", locked);
        super.write(compound);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        if (wallet == null) prepareWalletIfNeeded();
        wallet.read(nbt.getCompound("wallet"));
        locked = nbt.getBoolean("locked");

        register();
    }

    public void register() {
        AccountManager.register(this);
    }

    @Override
    public void tick() {
        if (world.isRemote) return; // Server only
        if (openedToken == null) return;

        openedToken.serverLifetimeTick--;
        if (openedToken.serverLifetimeTick <= 0) {
            // The token has expired, so we set it to null
            // It will not be possible anymore to access the wallet with this token
            openedToken = null;
        }
    }

    public void onDestroy() {
        AccountManager.unregister(this);
        wallet.verified = false; // unvalidate the wallet if the tileentity failed to unregister
    }

    public enum TransactionResponse {
        Unknown,
        Success,
        InvalidWallet,
        Refused,
        Locked
    }
}
