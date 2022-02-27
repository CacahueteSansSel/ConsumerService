package studios.nightek.consume.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import studios.nightek.consume.accounting.AccountManager;
import studios.nightek.consume.accounting.AccountUtilities;
import studios.nightek.consume.network.ConsumerNetwork;
import studios.nightek.consume.network.packets.ClientToServerApplyPackagerPricePacket;
import studios.nightek.consume.network.packets.ClientToServerProcessPaymentPacket;

import java.awt.*;

public class PackagerContainerScreen extends ContainerScreen<PackagerContainer> {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("consume", "textures/gui/packager.png");
    private static final ResourceLocation APPLY_TEXTURE = new ResourceLocation("consume", "textures/gui/apply_button.png");

    public static final int PRICE_TEXT_X = 116;
    public static final int PRICE_TEXT_Y = 18;

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
        applyButton = new ImageButton(windowX + 141, windowY + 47, 18, 18, 0, 0, 18, APPLY_TEXTURE, (btn) -> {
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
        final float LABEL_XPOS = 7;
        final float FONT_Y_SPACING = 11;

        final float PLAYER_INV_LABEL_YPOS = ProtectiveShelfContainer.PLAYER_INVENTORY_YPOS - FONT_Y_SPACING;
        this.font.drawText(matrixStack, this.playerInventory.getDisplayName(),
                LABEL_XPOS, PLAYER_INV_LABEL_YPOS, Color.darkGray.getRGB());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);   // this.minecraft.getTextureManager()

        this.blit(matrixStack, windowX, windowY, 0, 0, this.xSize, this.ySize);
    }
}


