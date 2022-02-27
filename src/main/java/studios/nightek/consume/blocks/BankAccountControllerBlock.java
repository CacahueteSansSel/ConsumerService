package studios.nightek.consume.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import studios.nightek.consume.ConsumerItems;
import studios.nightek.consume.accounting.Account;
import studios.nightek.consume.entities.BankAccountControllerBlockTileEntity;

import javax.annotation.Nullable;

public class BankAccountControllerBlock extends ProtectiveBlockBase {

    boolean lastPowered;
    public BankAccountControllerBlock() {
        super("blockchain_block");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BankAccountControllerBlockTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.isRemote) return;
        boolean power = worldIn.isBlockPowered(pos);
        if (power != lastPowered) {
            BankAccountControllerBlockTileEntity te = (BankAccountControllerBlockTileEntity)worldIn.getTileEntity(pos);
            te.setLocked(power);
            lastPowered = power;
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isRemote) return;
        BankAccountControllerBlockTileEntity te = (BankAccountControllerBlockTileEntity)worldIn.getTileEntity(pos);

        Account wallet = te.prepareWalletIfNeeded();
        te.register();
        int passcode = worldIn.rand.nextInt(9999);
        wallet.setPasscode(passcode);
        if (placer instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)placer;
            player.sendStatusMessage(new StringTextComponent(TextFormatting.GOLD.toString() + "Created new account in block at X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ()), false);
            player.sendStatusMessage(new StringTextComponent("Account details (keep confidential) :"), false);
            player.sendStatusMessage(new StringTextComponent("Account Id : " + wallet.id), false);
            player.sendStatusMessage(new StringTextComponent("Secret Code : " + String.format("%04d", passcode)), false);
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) return ActionResultType.PASS; // Server only

        BankAccountControllerBlockTileEntity te = (BankAccountControllerBlockTileEntity)worldIn.getTileEntity(pos);
        if (te.getWallet() == null) return ActionResultType.FAIL;
        ItemStack curItem = player.inventory.getCurrentItem();
        if (curItem.getItem() == Items.NAME_TAG && curItem.hasDisplayName()) {
            te.setWalletDisplayName(curItem.getDisplayName().getString());
            return ActionResultType.SUCCESS;
        } else if (curItem.getItem() == ConsumerItems.ROOTKIT) {
            if (te.getWallet() == null) {
                player.sendStatusMessage(new StringTextComponent("Account unreachable"), false);

                return ActionResultType.SUCCESS;
            }

            player.sendStatusMessage(new StringTextComponent("Account ID : " + te.getWallet().id), false);
            player.sendStatusMessage(new StringTextComponent("Account Name : " + te.getWallet().name), false);
            player.sendStatusMessage(new StringTextComponent("Secret Code Hash : " + te.getWallet().getPasscodeHash()), false);

            return ActionResultType.SUCCESS;
        }
        ItemStack card = te.getWallet().newCryptoCard();
        if (player.inventory.hasItemStack(card)) return ActionResultType.PASS;
        player.inventory.addItemStackToInventory(card);
        player.sendStatusMessage(new TranslationTextComponent("block.consume.blockchain_block.given_card"), true);

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        if (worldIn.isRemote) return; // Server only

        player.sendStatusMessage(new TranslationTextComponent("block.consume.blockchain_block.wallet_destroyed"), true);
        BankAccountControllerBlockTileEntity te = (BankAccountControllerBlockTileEntity)worldIn.getTileEntity(pos);
        te.onDestroy();
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        super.onExplosionDestroy(worldIn, pos, explosionIn);
        if (worldIn.isRemote) return; // Server only

        BankAccountControllerBlockTileEntity te = (BankAccountControllerBlockTileEntity)worldIn.getTileEntity(pos);
        te.onDestroy();
    }
}
