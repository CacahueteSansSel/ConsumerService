package studios.nightek.consume.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import studios.nightek.consume.accounting.AccountManager;
import studios.nightek.consume.items.ReceiptItem;

import java.awt.*;

public class ReceiptScreen extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("consume", "textures/gui/receipt.png");

    String[] receiptText;
    int receiptAmount;
    int windowX = 0;
    int windowY = 0;

    protected int xSize = 176;
    protected int ySize = 166;

    public ReceiptScreen(ItemStack receiptItem) {
        super(new TranslationTextComponent("ui.consume.receipt.title"));

        receiptText = ReceiptItem.getReceiptItemDescription(receiptItem).split(";");
        receiptAmount = ReceiptItem.getReceiptItemAmount(receiptItem);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        windowX = (this.width - this.xSize) / 2;
        windowY = (this.height - this.ySize) / 2;
        initUI();
    }

    void initUI() {

    }

    void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.font.drawText(matrixStack, this.title,
                windowX + 8, windowY + 10, Color.gray.getRGB());

        int lineIdx = 0;
        int maxLineCount = 112 / this.font.FONT_HEIGHT;
        for (String line : receiptText) {
            if (lineIdx >= maxLineCount) break;

            this.font.drawText(matrixStack, new StringTextComponent(line), windowX + 8, windowY + 25 +
                    (this.font.FONT_HEIGHT + 1) * lineIdx, Color.black.getRGB());

            lineIdx++;
        }

        String text = receiptAmount + " " + AccountManager.CURRENCY_NAME;
        int strWidth = font.getStringWidth(text);
        this.font.drawText(matrixStack, new StringTextComponent(text), windowX + 98 + (68 / 2 - strWidth / 2), windowY + 141 + (15 / 2 - font.FONT_HEIGHT / 2), Color.black.getRGB());
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);   // this.minecraft.getTextureManager()

        this.blit(matrixStack, windowX, windowY, 0, 0, this.xSize, this.ySize);

        renderForeground(matrixStack, mouseX, mouseY, partialTicks);
    }
}
