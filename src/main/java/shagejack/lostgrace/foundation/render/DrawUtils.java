package shagejack.lostgrace.foundation.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import shagejack.lostgrace.foundation.utility.Vector3;

import java.awt.Color;
import java.util.List;

public class DrawUtils {

    private DrawUtils() {
        throw new IllegalStateException(this.getClass().toString() + "should not be instantiated as it's a utility class.");
    }

    // Too many method overloads...
    public static void renderQuad(VertexConsumer builder, PoseStack renderStack, Vector3 pos, Vector3 facingNormal, float scale, TextureAtlasSprite sprite) {
        renderQuad(builder, renderStack, pos, facingNormal, scale, sprite, LightTexture.FULL_BRIGHT);
    }

    public static void renderQuad(VertexConsumer builder, PoseStack renderStack, Vector3 pos, Vector3 facingNormal, float scale, TextureAtlasSprite sprite, int alpha) {
        renderQuad(builder, renderStack, pos, facingNormal, scale, sprite, LightTexture.FULL_BRIGHT, alpha);
    }

    public static void renderQuad(VertexConsumer builder, PoseStack renderStack, Vector3 pos, Vector3 facingNormal, float scale, TextureAtlasSprite sprite, int r, int g, int b, int alpha) {
        renderQuad(builder, renderStack, pos, facingNormal, scale, sprite, LightTexture.FULL_BRIGHT, r, g, b, alpha);
    }

    public static void renderQuad(VertexConsumer builder, PoseStack renderStack, Vector3 pos, Vector3 facingNormal, float scale, TextureAtlasSprite sprite, int lightMapUV, int alpha) {
        renderQuad(builder, renderStack, pos, facingNormal, scale, sprite, lightMapUV, 255, 255, 255, alpha);
    }

    public static void renderQuad(VertexConsumer builder, PoseStack renderStack, Vector3 pos, Vector3 facingNormal, float scale, TextureAtlasSprite sprite, int lightMapUV, int r, int g, int b, int alpha) {
        renderQuad(builder, renderStack, pos, facingNormal, scale, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), lightMapUV, r, g, b, alpha);
    }

    public static void renderQuad(VertexConsumer builder, PoseStack renderStack, Vector3 pos, Vector3 facingNormal, float scale, float u0, float v0, float u1, float v1) {
        renderQuad(builder, renderStack, pos, facingNormal, scale, u0, v0, u1, v1, LightTexture.FULL_BRIGHT);
    }

    public static void renderQuad(VertexConsumer builder, PoseStack renderStack, Vector3 pos, Vector3 facingNormal, float scale, float u0, float v0, float u1, float v1, int lightMapUV) {
        renderQuad(builder, renderStack, pos, facingNormal, scale, u0, v0, u1, v1, lightMapUV, 255, 255, 255, 255);
    }

    public static void renderQuad(VertexConsumer builder, PoseStack renderStack, Vector3 pos, Vector3 facingNormal, float scale, float u0, float v0, float u1, float v1, int lightMapUV, int r, int g, int b, int alpha) {

        Vector3 pos1, pos2, pos3, pos4;

        if (!facingNormal.isParallelTo(Vector3.Z_NEG_AXIS)) {
            Quaternion rotation = Vector3.Z_NEG_AXIS.asToVecRotation(facingNormal);

            pos1 = pos.add(new Vector3(-scale, -scale, 0).transform(rotation));
            pos2 = pos.add(new Vector3(-scale, scale, 0).transform(rotation));
            pos3 = pos.add(new Vector3(scale, scale, 0).transform(rotation));
            pos4 = pos.add(new Vector3(scale, -scale, 0).transform(rotation));
        } else {
            pos1 = pos.add(-scale, -scale, 0);
            pos2 = pos.add(-scale, scale, 0);
            pos3 = pos.add(scale, scale, 0);
            pos4 = pos.add(scale, -scale, 0);
        }

        Matrix4f renderMatrix = renderStack.last().pose();

        pos1.drawPosVertex(renderMatrix, builder).color(r, g, b, alpha).uv(u0, v0).uv2(lightMapUV).normal(facingNormal.xF(), facingNormal.yF(), facingNormal.zF()).endVertex();
        pos2.drawPosVertex(renderMatrix, builder).color(r, g, b, alpha).uv(u0, v1).uv2(lightMapUV).normal(facingNormal.xF(), facingNormal.yF(), facingNormal.zF()).endVertex();
        pos3.drawPosVertex(renderMatrix, builder).color(r, g, b, alpha).uv(u1, v1).uv2(lightMapUV).normal(facingNormal.xF(), facingNormal.yF(), facingNormal.zF()).endVertex();
        pos4.drawPosVertex(renderMatrix, builder).color(r, g, b, alpha).uv(u1, v0).uv2(lightMapUV).normal(facingNormal.xF(), facingNormal.yF(), facingNormal.zF()).endVertex();
    }

    public static void renderTriangleWithColor(VertexConsumer builder, PoseStack renderStack, Vector3 pos, TriangleFace triangle, Color color, int alpha) {
        renderTriangleWithColor(builder, renderStack, pos, triangle, LightTexture.FULL_BRIGHT, color, alpha);
    }

    public static void renderTriangleWithColor(VertexConsumer builder, PoseStack renderStack, Vector3 pos, TriangleFace triangle, int lightMapUV, Color color, int alpha) {
        Matrix4f renderMatrix = renderStack.last().pose();

        int r, g, b;
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();

        pos.add(triangle.getV1()).drawPosVertex(renderMatrix, builder).color(r, g, b, alpha).uv2(lightMapUV).endVertex();
        pos.add(triangle.getV2()).drawPosVertex(renderMatrix, builder).color(r, g, b, alpha).uv2(lightMapUV).endVertex();
        pos.add(triangle.getV3()).drawPosVertex(renderMatrix, builder).color(r, g, b, alpha).uv2(lightMapUV).endVertex();
    }

    public static void renderSphere(PoseStack renderStack, Vector3 pos, double radius, Color color, int alpha) {
        renderSphere(renderStack, pos, radius, LightTexture.FULL_BRIGHT, color, alpha);
    }

    public static void renderSphere(PoseStack renderStack, Vector3 pos, double radius, int lightMapUV, Color color, int alpha) {
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(RenderTypeLG.SPHERE);

        List<TriangleFace> sphereFaces = new SphereBuilder().build(radius, 16);

        for (TriangleFace face : sphereFaces) {
            renderTriangleWithColor(builder, renderStack, pos, face, lightMapUV, color, alpha);
        }

        buffer.endBatch(RenderTypeLG.SPHERE);
    }

    public static int renderInLevelText(PoseStack renderStack, Vector3 pos, String text, Color color, float scale) {
        return renderInLevelText(renderStack, pos, text, color, scale, true);
    }

    public static int renderInLevelText(PoseStack renderStack, Vector3 pos, String text, Color color, float scale, boolean alwaysFacingPlayer) {
        Font font = Minecraft.getInstance().font;

        renderStack.pushPose();
        renderStack.translate(pos.x(), pos.y(), pos.z());
        renderStack.scale(scale, -scale, scale);

        if (alwaysFacingPlayer) {
            Quaternion rotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
            renderStack.mulPose(rotation);
        }

        int drawLength = font.drawShadow(renderStack, text, font.width(text) / 2.0f, font.lineHeight, color.getRGB());

        renderStack.popPose();

        return drawLength;
    }

}
