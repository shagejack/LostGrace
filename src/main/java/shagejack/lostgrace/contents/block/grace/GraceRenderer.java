package shagejack.lostgrace.contents.block.grace;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.foundation.render.RenderTypeLG;
import shagejack.lostgrace.foundation.tileEntity.renderer.SafeTileEntityRenderer;

public class GraceRenderer extends SafeTileEntityRenderer<GraceTileEntity> {

    public static final ResourceLocation HUMANITY = LostGrace.asResource("block/grace/humanity");

    public GraceRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(GraceTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (!te.getBlockState().isAir())
            renderGrace(te, partialTicks, ms, buffer, light, overlay);
    }

    protected void renderGrace(GraceTileEntity grace, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        int brightness = LightTexture.FULL_BRIGHT;

        if (grace.getSummonRemainingTicks() > 0) {
            float treeScale = 5.0f;

            // render golden tree
            // ms.pushPose();

            // ms.popPose();
        }

        float s = (System.currentTimeMillis() % 1000) / 1000.0f;
        if (s > 0.5f) {
            s = 1.0f - s;
        }
        float scale = 0.5f + s * 0.1f;

        TextureAtlasSprite spriteHumanity = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(HUMANITY);
        
        ms.pushPose();

        ms.translate(0.5, 0.5, 0.5);

        Quaternion rotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        ms.mulPose(rotation);

        VertexConsumer buffer = bufferSource.getBuffer(RenderTypeLG.GRACE);
        Matrix4f matrix = ms.last().pose();

        // TODO: do shear

        buffer.vertex(matrix, -scale, -scale, 0.0f).color(1.0f, 1.0f, 1.0f, 0.8f).uv(spriteHumanity.getU0(), spriteHumanity.getV0()).uv2(brightness).normal(1,0,0).endVertex();
        buffer.vertex(matrix, -scale, scale, 0.0f).color(1.0f, 1.0f, 1.0f, 0.8f).uv(spriteHumanity.getU0(), spriteHumanity.getV1()).uv2(brightness).normal(1,0,0).endVertex();
        buffer.vertex(matrix, scale, scale, 0.0f).color(1.0f, 1.0f, 1.0f, 0.8f).uv(spriteHumanity.getU1(), spriteHumanity.getV1()).uv2(brightness).normal(1,0,0).endVertex();
        buffer.vertex(matrix, scale, -scale, 0.0f).color(1.0f, 1.0f, 1.0f, 0.8f).uv(spriteHumanity.getU1(), spriteHumanity.getV0()).uv2(brightness).normal(1,0,0).endVertex();

        ms.popPose();
    }

}
