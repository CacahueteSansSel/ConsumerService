package studios.nightek.consume.bank;

import net.minecraft.util.ResourceLocation;

public class BankResources {
    public static final ResourceLocation BANK_LOGO_LOCATION = loadTexture("logo.png");

    static ResourceLocation loadTexture(String path) {
        return new ResourceLocation("consume", "textures/gui/bank/" + path);
    }
}
