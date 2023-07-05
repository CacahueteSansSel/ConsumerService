package dev.cacahuete.consume.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.cacahuete.consume.network.packets.ClientToServerApplyPackagerPricePacket;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import dev.cacahuete.consume.accounting.AccountUtilities;
import dev.cacahuete.consume.network.ConsumerNetwork;

import java.awt.*;

public class PackagerContainerScreen extends ContainerScreen<PackagerContainer> {

    private static final ResourceLocation BackgroundTexture = new ResourceLocation("consume", "textures/gui/packager.png");
    private static final ResourceLocation ApplyTexture = new ResourceLocation("consume", "textures/gui/apply_button.png");

    private static final int PriceTextX = 116;
    private static final int PriceTextY = 18;

    int windowX = 0;
    int windowY = 0;

    ImageButton applyButton;
    TextFieldWidget inputPrice;

    public PackagerContainerScreen(PackagerContainer cont, PlayerInventory playerInventory, ITextComponent title) {
        super(cont, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);

        windowX = (this.width - this.xSize) / 2;
        windowY = (this.height - this.ySize) / 2;

        initUI();
    }

    @Override
    public void onClose() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        super.onClose();
    }

    public void applyPrice() {
        if (!AccountUtilities.isInteger(inputPrice.getText())) return;

        ConsumerNetwork.channel.sendToServer(new ClientToServerApplyPackagerPricePacket(container.tileEntityPos, Integer.parseInt(inputPrice.getText())));
    }

    void initUI() {
        inputPrice = new TextFieldWidget(font, windowX + 120, windowY + 19, 37, 9, new StringTextComponent("0"));
        inputPrice.setEnableBackgroundDrawing(false);
        inputPrice.setMaxStringLength(4);
        inputPrice.setTextColor(Color.green.getRGB());
        applyButton = new ImageButton(windowX + 141, windowY + 47, 18, 18, 0, 0, 18, ApplyTexture, (btn) -> {
            applyPrice();
        });

        addButton(applyButton);
        children.add(inputPrice);

        this.setFocusedDefault(inputPrice);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);

        inputPrice.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        final float labelX = 7;
        final float fontY = 11;

        final float playerInventoryLabelY = ProtectiveShelfContainer.PlayerInventoryY - fontY;
        this.font.drawText(matrixStack, this.playerInventory.getDisplayName(),
                labelX, playerInventoryLabelY, Color.darkGray.getRGB());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bindTexture(BackgroundTexture);   // this.minecraft.getTextureManager()

        this.blit(matrixStack, windowX, windowY, 0, 0, this.xSize, this.ySize);
    }
}


