package studios.nightek.consume.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import studios.nightek.consume.accounting.AccountManager;
import studios.nightek.consume.accounting.AccountAccessToken;
import studios.nightek.consume.accounting.Account;
import studios.nightek.consume.accounting.AccountUtilities;
import studios.nightek.consume.network.packets.ServerToClientCryptoLoginResponsePacket;

import java.awt.*;

public class ATMScreen extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("consume", "textures/gui/atm_center.png");

    protected int xSize = 176;
    protected int ySize = 166;
    protected int windowX;
    protected int windowY;
    AccountAccessToken token;
    Account wallet;
    MenuBase[] atmMenus = new MenuBase[5];
    int curMenuIdx = 0;
    String walletName;

    Button continueButton;

    public ATMScreen(ServerToClientCryptoLoginResponsePacket packet) {
        super(new StringTextComponent(""));
        token = packet.token;
        wallet = packet.wallet;
        walletName = wallet.getDisplayName();
    }

    @Override
    protected void init() {
        super.init();
        windowX = (this.width - this.xSize) / 2;
        windowY = (this.height - this.ySize) / 2;
        initUI();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    void initSideButtons() {
        Button sideButtonRelogin = new Button(windowX - 66, windowY, 60, 20, new TranslationTextComponent("ui.consume.atm.relogin"), this::buttonRelogin);
        addButton(sideButtonRelogin);
        Button sideButtonRename = new Button(windowX - 66, windowY + 30, 60, 20, new TranslationTextComponent("ui.consume.atm.rename"), this::buttonRename);
        addButton(sideButtonRename);
        Button sideButtonDisconnect = new Button(windowX - 66, windowY + 60, 60, 20, new TranslationTextComponent("ui.consume.atm.quit"), this::buttonLogoff);
        addButton(sideButtonDisconnect);

        Button sideButtonWithdraw = new Button(windowX + 183, windowY, 60, 20, new TranslationTextComponent("ui.consume.atm.withdraw"), this::buttonWithdraw);
        addButton(sideButtonWithdraw);
        Button sideButtonDeposit = new Button(windowX + 183, windowY + 30, 60, 20, new TranslationTextComponent("ui.consume.atm.deposit"), this::buttonDeposit);
        addButton(sideButtonDeposit);
        Button sideButtonSend = new Button(windowX + 183, windowY + 60, 60, 20, new TranslationTextComponent("ui.consume.atm.transfer"), this::buttonTransfer);
        addButton(sideButtonSend);

        continueButton = new Button(windowX + 14 + 171, windowY + 64 + 83, 60, 20, new TranslationTextComponent("ui.consume.crypto.login.continue"), this::buttonContinueClicked);
        addButton(continueButton);
    }

    void registerMenuAt(int idx, MenuBase menu) {
        atmMenus[idx] = menu;
        atmMenus[idx].init(this, this.font, windowX + 14, windowY + 64);
    }

    void initUI() {
        registerMenuAt(0, new HomeMenu());
        registerMenuAt(1, new RenameWalletMenu());
        registerMenuAt(2, new WithdrawWalletMenu());
        registerMenuAt(3, new DepositWalletMenu());
        registerMenuAt(4, new TransferMenu());

        setMenu(0);
        initSideButtons();
    }

    private void buttonContinueClicked(Button button) {
        MenuBase curMenu = atmMenus[curMenuIdx];

        if (curMenu != null) curMenu.continueButtonClicked();
    }

    void buttonRelogin(Button btn) {
        minecraft.displayGuiScreen(new CryptoLoginScreen((scr, packet) -> {
            if (packet.isError) {
                minecraft.displayGuiScreen(new CryptoResponseScreen(packet));
            } else {
                token = packet.token;
                wallet = packet.wallet;
                minecraft.displayGuiScreen(this);
            }
        }));
    }

    void buttonLogoff(Button btn) {
        closeScreen();
        AccountManager.ATM.disconnect(token);
    }

    void buttonRename(Button btn) {
        setMenu(1);
    }

    void buttonWithdraw(Button btn) {
        setMenu(2);
    }

    void buttonDeposit(Button btn) {
        setMenu(3);
    }

    void buttonTransfer(Button btn) {
        setMenu(4);
    }

    public void setMenu(int id) {
        atmMenus[curMenuIdx].disable();
        curMenuIdx = id;
        atmMenus[curMenuIdx].enable();
    }

    void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.font.drawText(matrixStack, new StringTextComponent(wallet.getDisplayName()),
                windowX + 18, windowY + 18, Color.MAGENTA.getRGB());
        this.font.drawText(matrixStack, new StringTextComponent(wallet.getAmountDisplayable()),
                windowX + 18, windowY + 31, Color.MAGENTA.getRGB());

        MenuBase curMenu = atmMenus[curMenuIdx];

        if (curMenu != null) {
            this.font.drawText(matrixStack, curMenu.getTitle(),
                    windowX + 14, windowY + 52, Color.MAGENTA.getRGB());

            curMenu.render(matrixStack, mouseX, mouseY, partialTicks, windowX + 14, windowY + 64);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);

        this.blit(matrixStack, windowX, windowY, 0, 0, this.xSize, this.ySize);

        renderForeground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public abstract class MenuBase {
        public abstract ITextComponent getTitle();
        public abstract void init(ATMScreen screen, FontRenderer font, int xOff, int yOff);
        public abstract void render(MatrixStack matrix, int mx, int my, float partialTicks, int xOff, int yOff);

        public abstract void enable();
        public abstract void disable();
        public abstract void continueButtonClicked();
    }

    static int parseInt(String v) {
        try {
            int i = Integer.parseInt(v);
            return i;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public class HomeMenu extends MenuBase {
        @Override
        public ITextComponent getTitle() {
            return new TranslationTextComponent("ui.consume.atm.choose_option");
        }

        @Override
        public void init(ATMScreen screen, FontRenderer font, int xOff, int yOff) {

        }

        @Override
        public void render(MatrixStack matrix, int mx, int my, float partialTicks, int xOff, int yOff) {

        }

        @Override
        public void enable() {

        }

        @Override
        public void disable() {

        }

        @Override
        public void continueButtonClicked() {

        }
    }

    public class TransferMenu extends MenuBase {
        TextFieldWidget targetWalletIDField;
        TextFieldWidget amountField;
        ATMScreen screen;
        ITextComponent walletIdTxt = new TranslationTextComponent("ui.consume.atm.target_account_id");
        ITextComponent amountTxt = new TranslationTextComponent("ui.consume.atm.amount");
        @Override
        public ITextComponent getTitle() {
            return new TranslationTextComponent("ui.consume.atm.transfer");
        }

        @Override
        public void init(ATMScreen screen, FontRenderer font, int xOff, int yOff) {
            this.screen = screen;
            targetWalletIDField = new TextFieldWidget(font, xOff, yOff + 11, 147, 20, new StringTextComponent(""));
            amountField = new TextFieldWidget(font, xOff, yOff + 45, 147, 20, new StringTextComponent(""));
        }

        @Override
        public void enable() {
            targetWalletIDField.changeFocus(true);
            targetWalletIDField.setCanLoseFocus(false);
            children.add(targetWalletIDField);
            children.add(amountField);
        }

        @Override
        public void disable() {
            targetWalletIDField.changeFocus(false);
            amountField.changeFocus(false);
            targetWalletIDField.setCanLoseFocus(true);
            children.remove(targetWalletIDField);
            children.remove(amountField);
        }

        @Override
        public void render(MatrixStack matrix, int mx, int my, float partialTicks, int xOff, int yOff) {
            font.drawText(matrix, walletIdTxt, xOff, yOff, Color.white.getRGB());
            font.drawText(matrix, amountTxt, xOff, yOff + 35, Color.white.getRGB());

            targetWalletIDField.render(matrix, mx, my, partialTicks);
            amountField.render(matrix, mx, my, partialTicks);
        }

        @Override
        public void continueButtonClicked() {
            int amount = parseInt(amountField.getText());
            if (amount <= 0) return;

            AccountManager.ATM.send(token, targetWalletIDField.getText(), amount, (packet) -> {
                if (!packet.isError) walletName = packet.walletName;
                minecraft.displayGuiScreen(new CryptoResponseScreen(packet).withFallbackScreen(screen));
                return true;
            });
        }
    }

    public class DepositWalletMenu extends MenuBase {
        TextFieldWidget amountField;
        ATMScreen screen;
        ITextComponent newNameTxt = new TranslationTextComponent("ui.consume.atm.amount");
        @Override
        public ITextComponent getTitle() {
            return new TranslationTextComponent("ui.consume.atm.deposit");
        }

        @Override
        public void init(ATMScreen screen, FontRenderer font, int xOff, int yOff) {
            this.screen = screen;
            amountField = new TextFieldWidget(font, xOff, yOff + 11, 147, 20, new StringTextComponent(wallet.getDisplayName()));
        }

        @Override
        public void enable() {
            amountField.changeFocus(true);
            amountField.setCanLoseFocus(false);
            children.add(amountField);
        }

        @Override
        public void disable() {
            amountField.changeFocus(false);
            amountField.setCanLoseFocus(true);
            children.remove(amountField);
        }

        @Override
        public void render(MatrixStack matrix, int mx, int my, float partialTicks, int xOff, int yOff) {
            amountField.render(matrix, mx, my, partialTicks);
            font.drawText(matrix, newNameTxt, xOff, yOff, Color.white.getRGB());
        }

        @Override
        public void continueButtonClicked() {
            if (!AccountUtilities.isInteger(amountField.getText())) return;

            int amount = parseInt(amountField.getText());
            if (amount <= 0) return;

            AccountManager.ATM.deposit(token, amount, (packet) -> {
                minecraft.displayGuiScreen(new CryptoResponseScreen(packet).withFallbackScreen(screen));
                return true;
            });
        }
    }

    public class WithdrawWalletMenu extends MenuBase {
        TextFieldWidget amountField;
        ATMScreen screen;
        ITextComponent newNameTxt = new TranslationTextComponent("ui.consume.atm.amount");
        @Override
        public ITextComponent getTitle() {
            return new TranslationTextComponent("ui.consume.atm.withdraw");
        }

        @Override
        public void init(ATMScreen screen, FontRenderer font, int xOff, int yOff) {
            this.screen = screen;
            amountField = new TextFieldWidget(font, xOff, yOff + 11, 147, 20, new StringTextComponent(wallet.getDisplayName()));
        }

        @Override
        public void enable() {
            amountField.changeFocus(true);
            amountField.setCanLoseFocus(false);
            children.add(amountField);
        }

        @Override
        public void disable() {
            amountField.changeFocus(false);
            amountField.setCanLoseFocus(true);
            children.remove(amountField);
        }

        @Override
        public void render(MatrixStack matrix, int mx, int my, float partialTicks, int xOff, int yOff) {
            amountField.render(matrix, mx, my, partialTicks);
            font.drawText(matrix, newNameTxt, xOff, yOff, Color.white.getRGB());
        }

        @Override
        public void continueButtonClicked() {
            if (!AccountUtilities.isInteger(amountField.getText())) return;

            int amount = parseInt(amountField.getText());
            if (amount <= 0) return;

            AccountManager.ATM.withdraw(token, amount, (packet) -> {
                minecraft.displayGuiScreen(new CryptoResponseScreen(packet).withFallbackScreen(screen));
                return true;
            });
        }
    }

    public class RenameWalletMenu extends MenuBase {
        TextFieldWidget newNameField;
        ATMScreen screen;
        ITextComponent newNameTxt = new TranslationTextComponent("ui.consume.atm.new_name");
        @Override
        public ITextComponent getTitle() {
            return new TranslationTextComponent("ui.consume.atm.rename");
        }

        @Override
        public void init(ATMScreen screen, FontRenderer font, int xOff, int yOff) {
            this.screen = screen;
            newNameField = new TextFieldWidget(font, xOff, yOff + 11, 147, 20, new StringTextComponent(wallet.getDisplayName()));
        }

        @Override
        public void enable() {
            newNameField.changeFocus(true);
            newNameField.setCanLoseFocus(false);
            children.add(newNameField);
        }

        @Override
        public void disable() {
            newNameField.changeFocus(false);
            newNameField.setCanLoseFocus(true);
            children.remove(newNameField);
        }

        @Override
        public void render(MatrixStack matrix, int mx, int my, float partialTicks, int xOff, int yOff) {
            newNameField.render(matrix, mx, my, partialTicks);
            font.drawText(matrix, newNameTxt, xOff, yOff, Color.white.getRGB());
        }

        @Override
        public void continueButtonClicked() {
            AccountManager.ATM.rename(token, newNameField.getText(), (packet) -> {
                if (!packet.isError) walletName = packet.walletName;
                minecraft.displayGuiScreen(new CryptoResponseScreen(packet).withFallbackScreen(screen));
                return true;
            });
        }
    }
}
