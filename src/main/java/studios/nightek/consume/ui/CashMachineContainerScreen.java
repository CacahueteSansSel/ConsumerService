package studios.nightek.consume.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import studios.nightek.consume.accounting.AccountManager;

import java.awt.*;

public class CashMachineContainerScreen extends ContainerScreen<CashMachineContainer> {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("consume", "textures/gui/cash_machine.png");

    public static final int PRICE_TEXT_X = 116;
    public static final int PRICE_TEXT_Y = 18;

    int windowX = 0;
    int windowY = 0;

    public CashMachineContainerScreen(CashMachineContainer cont, PlayerInventory playerInventory, ITextComponent title) {
        super(cont, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        windowX = (this.width - this.xSize) / 2;
        windowY = (this.height - this.ySize) / 2;
        initUI();
    }

    void initUI() {
        addButton(new Button(windowX + 157, windowY + 63, 12, 12, new StringTextComponent("+"), (btn) -> {
            minecraft.displayGuiScreen(new CryptoLoginScreen((screen, packet) -> {
                if (packet.isError) {
                    minecraft.displayGuiScreen(new CryptoResponseScreen(packet).withFallbackScreen(this));
                } else {
                    AccountManager.clientProcessPaymentForTileEntity(packet.token, container.tileEntityPos, true, (paymentPacket) -> {
                        minecraft.displayGuiScreen(new CryptoResponseScreen(paymentPacket).withFallbackScreen(this));
                        return true;
                    });
                }
            }));
        }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     * Taken directly from ContainerScreen
     */
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        final float LABEL_XPOS = 5;
        final float FONT_Y_SPACING = 12;
        final float CHEST_LABEL_YPOS = ProtectiveShelfContainer.TILE_INVENTORY_YPOS - FONT_Y_SPACING + 2;
        this.font.drawText(matrixStack, this.title,
                LABEL_XPOS, 6, Color.darkGray.getRGB());

        final float PLAYER_INV_LABEL_YPOS = ProtectiveShelfContainer.PLAYER_INVENTORY_YPOS - FONT_Y_SPACING;
        this.font.drawText(matrixStack, this.playerInventory.getDisplayName(),
                LABEL_XPOS, PLAYER_INV_LABEL_YPOS, Color.darkGray.getRGB());

        String price = container.getTotalPriceInEmeralds() + " E";
        this.font.drawText(matrixStack, new StringTextComponent(price), PRICE_TEXT_X, PRICE_TEXT_Y, Color.green.getRGB());
    }

    /**
     * Draws the background layer of this container (behind the items).
     * Taken directly from ChestScreen / BeaconScreen
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);   // this.minecraft.getTextureManager()

        // width and height are the size provided to the window when initialised after creation.
        // xSize, ySize are the expected size of the texture-? usually seems to be left as a default.
        // The code below is typical for vanilla containers, so I've just copied that- it appears to centre the texture within
        //  the available window
        this.blit(matrixStack, windowX, windowY, 0, 0, this.xSize, this.ySize);
    }
}


