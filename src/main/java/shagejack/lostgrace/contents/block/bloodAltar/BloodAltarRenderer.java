package shagejack.lostgrace.contents.block.bloodAltar;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import shagejack.lostgrace.foundation.tile.renderer.SafeTileEntityRenderer;

public class BloodAltarRenderer extends SafeTileEntityRenderer<BloodAltarTileEntity> {

    public BloodAltarRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(BloodAltarTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderBlood(te, partialTicks, ms, buffer, light, overlay);
    }

    protected void renderBlood(BloodAltarTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {

    }
}
