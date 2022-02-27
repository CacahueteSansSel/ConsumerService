package studios.nightek.consume.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import studios.nightek.consume.accounting.AccountManager;
import studios.nightek.consume.network.packets.ServerToClientCryptoLoginResponsePacket;
import studios.nightek.consume.network.packets.ServerToClientPaymentResponsePacket;

import java.awt.*;

public class CryptoResponseScreen extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE_YES = new ResourceLocation("consume", "textures/gui/crypto_login_response_yes.png");
    private static final ResourceLocation BACKGROUND_TEXTURE_NO = new ResourceLocation("consume", "textures/gui/crypto_login_response_no.png");

    StringBuilder bld = new StringBuilder();
    TranslationTextComponent paymentText = new TranslationTextComponent("ui.consume.crypto.recap.payment");
    TranslationTextComponent paymentSucceeded = new TranslationTextComponent("ui.consume.crypto.recap.succeeded");
    TranslationTextComponent paymentFailed = new TranslationTextComponent("ui.consume.crypto.recap.failed");
    int windowX = 0;
    int windowY = 0;
    Button buttonContinue;
    String[] builderLines;
    boolean isError;
    Screen fallbackScreen;

    protected int xSize = 176;
    protected int ySize = 166;

    public CryptoResponseScreen(ServerToClientCryptoLoginResponsePacket packet) {
        super(new TranslationTextComponent("ui.consume.crypto.recap.title"));
        isError = packet.isError;

        // Build the info section
        if (packet.isError) {
            builderAppend(packet.message);
        } else {
            builderAppend("Issuer : " + packet.token.issuer);
        }

        builderLines = bld.toString().split(";");
    }

    public CryptoResponseScreen(ServerToClientPaymentResponsePacket packet) {
        super(new TranslationTextComponent("ui.consume.crypto.recap.title"));
        isError = packet.isError;

        if (isError) builderAppend(packet.message);

        if (packet.walletName != null) builderAppend("Wallet : " + packet.walletName);
        if (packet.amount > 0) {
            builderAppend("Amount : " + packet.amount + " " + AccountManager.CURRENCY_NAME);
        } else paymentText = new TranslationTextComponent("ui.consume.crypto.recap.process");
        builderAppend("Issuer : " + packet.issuer);

        builderLines = bld.toString().split(";");
    }

    public CryptoResponseScreen withFallbackScreen(Screen scr) {
        fallbackScreen = scr;
        return this;
    }

    void builderAppend(String line) {
        bld.append(line + ";");
    }

    @Override
    protected void init() {
        super.init();
        windowX = (this.width - this.xSize) / 2;
        windowY = (this.height - this.ySize) / 2;
        initUI();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    void initUI() {
        buttonContinue = new Button(windowX + 123, windowY + 140, 48, 18, new TranslationTextComponent("ui.consume.crypto.login.continue"), (btn) -> {
            if (fallbackScreen != null) {
                minecraft.displayGuiScreen(fallbackScreen);
            } else closeScreen();
        });

        addButton(buttonContinue);
    }

    void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.font.drawText(matrixStack, this.title,
                windowX + 7, windowY + 7, isError ? Color.red.getRGB() : Color.green.getRGB());
        this.font.drawText(matrixStack, paymentText,
                windowX + 62, windowY + 38, Color.white.getRGB());
        this.font.drawText(matrixStack, isError ? paymentFailed : paymentSucceeded,
                windowX + 62, windowY + 46, Color.white.getRGB());

        for (int y = 0; y < 4; y++) {
            if (y >= builderLines.length) break;

            int realY = windowY + 80 + y * 9;
            int realX = windowX + 20;
            this.font.drawText(matrixStack, new StringTextComponent(builderLines[y]), realX, realY, Color.white.getRGB());
        }

        this.buttonContinue.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(isError ? BACKGROUND_TEXTURE_NO : BACKGROUND_TEXTURE_YES);

        this.blit(matrixStack, windowX, windowY, 0, 0, this.xSize, this.ySize);

        renderForeground(matrixStack, mouseX, mouseY, partialTicks);
    }
}
