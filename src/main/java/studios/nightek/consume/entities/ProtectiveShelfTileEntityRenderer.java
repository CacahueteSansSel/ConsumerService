package studios.nightek.consume.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.util.math.vector.Quaternion;

public class ProtectiveShelfTileEntityRenderer extends TileEntityRenderer<ProtectiveShelfTileEntity> {
    public static final float ITEMS_SIZE = 0.75f;
    public ProtectiveShelfTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    float getMaxDistanceFromFanciness(Minecraft mc) {
        GraphicsFanciness fc = mc.gameSettings.graphicFanciness;

        switch (fc) {
            case FAST:
                return 20f;
            case FANCY:
                return 40f;
            case FABULOUS:
                return 80f;
        }

        return 50f;
    }

    @Override
    public void render(ProtectiveShelfTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Minecraft mc = Minecraft.getInstance();

        if (!tileEntityIn.getPos().withinDistance(mc.player.getPosition(), getMaxDistanceFromFanciness(mc)))
            return;

        matrixStackIn.scale(0.3f, 0.3f, 0.3f);
        matrixStackIn.translate(0.75f, 3.05f, -0.3f);
        matrixStackIn.rotate(new Quaternion(0, 90, 0, true));

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                matrixStackIn.translate(-1f, 0, 0);
                matrixStackIn.scale(ITEMS_SIZE, ITEMS_SIZE, ITEMS_SIZE);
                mc.getItemRenderer().renderItem(tileEntityIn.inv.getStackInSlot(y * 3 + x), ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
                matrixStackIn.scale(1 / ITEMS_SIZE, 1 / ITEMS_SIZE, 1 / ITEMS_SIZE);
                matrixStackIn.translate(0, 0, 1.75f);
                matrixStackIn.scale(ITEMS_SIZE, ITEMS_SIZE, ITEMS_SIZE);
                mc.getItemRenderer().renderItem(tileEntityIn.inv.getStackInSlot(y * 3 + x), ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
                matrixStackIn.scale(1 / ITEMS_SIZE, 1 / ITEMS_SIZE, 1 / ITEMS_SIZE);
                matrixStackIn.translate(0, 0, -1.75f);
            }
            matrixStackIn.translate(3f, -1f, 0);
        }
    }
}
