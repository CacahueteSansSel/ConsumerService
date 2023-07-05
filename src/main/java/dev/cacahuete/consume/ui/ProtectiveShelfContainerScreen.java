package dev.cacahuete.consume.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

public class ProtectiveShelfContainerScreen extends ContainerScreen<ProtectiveShelfContainer> {

    private static final ResourceLocation BackgroundTexture = new ResourceLocation("consume", "textures/gui/shop_shelf.png");

    public ProtectiveShelfContainerScreen(ProtectiveShelfContainer cont, PlayerInventory playerInventory, ITextComponent title) {
        super(cont, playerInventory, title);
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
        final float labelX = 8;
        final float fontY = 12;
        final float chestLabelY = ProtectiveShelfContainer.TileEntityInventoryY - fontY + 2;
        this.font.drawText(matrixStack, this.title,
                labelX, chestLabelY, Color.darkGray.getRGB());

        final float playerInvLabelY = ProtectiveShelfContainer.PlayerInventoryY - fontY;
        this.font.drawText(matrixStack, this.playerInventory.getDisplayName(),
                labelX, playerInvLabelY, Color.darkGray.getRGB());
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
        int edgeSpacingX = (this.width - this.xSize) / 2;
        int edgeSpacingY = (this.height - this.ySize) / 2;
        this.blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, this.xSize, this.ySize);
    }
}


