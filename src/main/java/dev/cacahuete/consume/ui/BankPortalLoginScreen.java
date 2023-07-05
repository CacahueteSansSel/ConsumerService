package dev.cacahuete.consume.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.cacahuete.consume.ConsumerItems;
import dev.cacahuete.consume.accounting.AccountManager;
import dev.cacahuete.consume.bank.BankResources;
import dev.cacahuete.consume.items.CreditCardItem;
import dev.cacahuete.consume.network.packets.ServerToClientCryptoLoginResponsePacket;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import dev.cacahuete.consume.accounting.AccountUtilities;

import java.awt.*;

public class BankPortalLoginScreen extends Screen {
    private static final ResourceLocation BackgroundTexture = new ResourceLocation("consume", "textures/gui/crypto_login.png");
    private static final ResourceLocation CardButtonTexture = new ResourceLocation("consume", "textures/gui/card_button.png");

    int windowX = 0;
    int windowY = 0;
    StringTextComponent dots = new StringTextComponent("...");
    TextFieldWidget inputWalletID;
    TextFieldWidget inputPasscode;
    Button buttonContinue;
    ImageButton buttonCard;
    CryptoLoginResponseListener listener;

    protected int xSize = 176;
    protected int ySize = 166;

    public BankPortalLoginScreen(CryptoLoginResponseListener listener) {
        super(new TranslationTextComponent("ui.consume.crypto.login.title"));
        this.listener = listener;
    }

    @Override
    protected void init() {
        super.init();
        windowX = (this.width - this.xSize) / 2;
        windowY = (this.height - this.ySize) / 2;
        initUI();
    }

    void drawTexture(MatrixStack matrixStack, ResourceLocation location, int x, int y, int width, int height, int tx, int ty) {
        this.minecraft.getTextureManager().bindTexture(location);

        this.blit(matrixStack, x, y, width, height, 0, 0, width, height, tx, ty);
    }

    boolean playerHasCard() {
        ClientPlayerEntity player = minecraft.player;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack cur = player.inventory.getStackInSlot(i);
            if (cur.getItem() == ConsumerItems.CREDIT_CARD) {
                return true;
            }
        }

        return false;
    }

    void initUI() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        inputWalletID = new TextFieldWidget(this.font, windowX + 20, windowY + 45, 114, 8, dots);
        inputPasscode = new TextFieldWidget(this.font, windowX + 20, windowY + 87, 134, 8, dots);
        inputWalletID.setEnableBackgroundDrawing(false);
        inputWalletID.setMaxStringLength(AccountUtilities.ACCOUNT_ID_MAX_LENGTH);
        inputPasscode.setEnableBackgroundDrawing(false);
        inputPasscode.setMaxStringLength(4);
        buttonContinue = new Button(windowX + 123, windowY + 140, 48, 18, new TranslationTextComponent("ui.consume.crypto.login.continue"), (btn) -> {
            if (inputWalletID.getText().length() == 0 || inputPasscode.getText().length() == 0) return;
            String walletId = inputWalletID.getText();
            if (!AccountUtilities.isInteger(inputPasscode.getText())) return;

            int passcode = Integer.parseInt(inputPasscode.getText());

            AccountManager.clientAccessWallet(walletId, passcode, (packet) -> {
                listener.listen(this, packet);
                return true;
            });
        });
        buttonCard = new ImageButton(windowX + 142, windowY + 40, 18, 18, 0, 0, 18, CardButtonTexture, (btn) -> {
            ClientPlayerEntity player = minecraft.player;
            ItemStack card = null;
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack cur = player.inventory.getStackInSlot(i);
                if (cur.getItem().getRegistryName().getPath().equals("crypto_card")) {
                    card = cur;
                    break;
                }
            }
            if (card == null || card.getTag() == null || !card.getTag().contains("wallet_id")) return;

            inputWalletID.setText(CreditCardItem.getCardWalletId(card));
        });

        addButton(buttonCard);
        addButton(buttonContinue);
        children.add(inputWalletID);
        children.add(inputPasscode);
        this.setFocusedDefault(this.inputWalletID);
    }

    void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.font.drawText(matrixStack, this.title,
                windowX + 7, windowY + 7, Color.cyan.getRGB());
        this.font.drawText(matrixStack, new TranslationTextComponent("ui.consume.crypto.login.walletid"),
                windowX + 20, windowY + 29, Color.white.getRGB());
        this.font.drawText(matrixStack, new TranslationTextComponent("ui.consume.crypto.login.passcode"),
                windowX + 20, windowY + 71, Color.white.getRGB());

        drawTexture(matrixStack, BankResources.BankLogoLocation, windowX + 10, windowY + 132, 128, 128, 128, 128);

        this.inputWalletID.render(matrixStack, mouseX, mouseY, partialTicks);
        this.inputPasscode.render(matrixStack, mouseX, mouseY, partialTicks);
        this.buttonContinue.render(matrixStack, mouseX, mouseY, partialTicks);
        this.buttonCard.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BackgroundTexture);   // this.minecraft.getTextureManager()

        this.blit(matrixStack, windowX, windowY, 0, 0, this.xSize, this.ySize);

        renderForeground(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @FunctionalInterface
    public interface CryptoLoginResponseListener {
        void listen(BankPortalLoginScreen screen, ServerToClientCryptoLoginResponsePacket packet);
    }
}
