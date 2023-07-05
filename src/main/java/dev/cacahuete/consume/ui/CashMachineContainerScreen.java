package dev.cacahuete.consume.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import dev.cacahuete.consume.accounting.AccountManager;

import java.awt.*;

// This is probably copy-pasted (stolen!) code from somewhere (I modified the naming slightly for this one)
// I don't have the source anymore but any help is accepted
// - Cacahuète
public class CashMachineContainerScreen extends ContainerScreen<CashMachineContainer> {

    private static final ResourceLocation BackgroundTexture = new ResourceLocation("consume", "textures/gui/cash_machine.png");
    private static final int PriceTextX = 116;
    private static final int PriceTextY = 18;

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
            minecraft.displayGuiScreen(new BankPortalLoginScreen((screen, packet) -> {
                if (packet.isError) {
                    minecraft.displayGuiScreen(new BankPortalResponseScreen(packet).withFallbackScreen(this));
                } else {
                    AccountManager.clientProcessPaymentForTileEntity(packet.token, container.tileEntityPos, true, (paymentPacket) -> {
                        minecraft.displayGuiScreen(new BankPortalResponseScreen(paymentPacket).withFallbackScreen(this));
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
        float labelX = 5;
        float fontY = 12;
        float chestLabelY = ProtectiveShelfContainer.TileEntityInventoryY - fontY + 2;
        this.font.drawText(matrixStack, this.title,
                labelX, 6, Color.darkGray.getRGB());

        float playerInvLabelY = ProtectiveShelfContainer.PlayerInventoryY - fontY;
        this.font.drawText(matrixStack, this.playerInventory.getDisplayName(),
                labelX, playerInvLabelY, Color.darkGray.getRGB());

        String price = container.getTotalPriceInEmeralds() + " SE";
        this.font.drawText(matrixStack, new StringTextComponent(price), PriceTextX, PriceTextY, Color.green.getRGB());
    }

    /**
     * Draws the background layer of this container (behind the items).
     * Taken directly from ChestScreen / BeaconScreen
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bindTexture(BackgroundTexture);   // this.minecraft.getTextureManager()

        // width and height are the size provided to the window when initialised after creation.
        // xSize, ySize are the expected size of the texture-? usually seems to be left as a default.
        // The code below is typical for vanilla containers, so I've just copied that- it appears to centre the texture within
        //  the available window
        this.blit(matrixStack, windowX, windowY, 0, 0, this.xSize, this.ySize);
    }
}


