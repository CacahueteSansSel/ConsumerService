package studios.nightek.consume.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import studios.nightek.consume.accounting.AccountManager;
import studios.nightek.consume.bank.BankResources;
import studios.nightek.consume.items.CreditCardItem;

import java.awt.*;

public class CreditCardScreen extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("consume", "textures/gui/credit_card.png");

    int windowX = 0;
    int windowY = 0;

    protected int xSize = 189;
    protected int ySize = 128;

    StringTextComponent cardAccountId;
    StringTextComponent cardName;

    public CreditCardScreen(ItemStack cardItem) {
        super(new StringTextComponent(""));

        cardAccountId = new StringTextComponent(CreditCardItem.getCardWalletId(cardItem));
        cardName = new StringTextComponent(CreditCardItem.getCardDisplayName(cardItem));
    }

    @Override
    protected void init() {
        super.init();
        windowX = (this.width - this.xSize) / 2;
        windowY = (this.height - this.ySize) / 2;
        initUI();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    void initUI() {

    }

    void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // Draw the bank's logo texture
        // The bank logo and all it's assets are separated to facilitate customization
        // So if anyone wants to show a custom bank, he can, and easily ;)

        drawTexture(matrixStack, BankResources.BANK_LOGO_LOCATION, windowX + 13, windowY + 13, 128, 128, 128, 128);

        // Draw the credit card's id
        font.drawTextWithShadow(matrixStack, cardAccountId, windowX + 16, windowY + 83, Color.white.getRGB());
        font.drawTextWithShadow(matrixStack, cardName, windowX + 16, windowY + 103, Color.white.getRGB());
    }

    void drawTexture(MatrixStack matrixStack, ResourceLocation location, int x, int y, int width, int height, int tx, int ty) {
        this.minecraft.getTextureManager().bindTexture(location);

        this.blit(matrixStack, x, y, width, height, 0, 0, width, height, tx, ty);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexture(matrixStack, BACKGROUND_TEXTURE, windowX, windowY, xSize, ySize, 256, 256);

        renderForeground(matrixStack, mouseX, mouseY, partialTicks);
    }
}
