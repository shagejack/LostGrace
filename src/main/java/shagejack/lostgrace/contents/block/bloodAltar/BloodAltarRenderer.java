package shagejack.lostgrace.contents.block.bloodAltar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import shagejack.lostgrace.foundation.render.DrawUtils;
import shagejack.lostgrace.foundation.render.FluidRenderer;
import shagejack.lostgrace.foundation.render.RenderTypeLG;
import shagejack.lostgrace.foundation.tile.renderer.SafeTileEntityRenderer;
import shagejack.lostgrace.foundation.utility.Vector3;
import shagejack.lostgrace.registries.AllTextures;

import java.awt.*;
import java.util.List;

public class BloodAltarRenderer extends SafeTileEntityRenderer<BloodAltarTileEntity> {

    public BloodAltarRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(BloodAltarTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderBlood(te, partialTicks, ms, buffer, light, overlay);

        BloodAltarPhase phase = te.getPhase();
        int tick = BloodAltarTileEntity.TOTAL_TICKS - te.getRemainingTicks();

        if (tick > 0)
            renderOther(te, phase, tick, partialTicks, ms, buffer, light, overlay);
    }

    protected void renderBlood(BloodAltarTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (te.bloodTank.isEmpty())
            return;

        int amount = te.bloodTank.getFluidAmount();

        final float xMin, xMax, yMin, yMax, zMin, zMax;

        if (amount <= te.getCapacity() / 4) {
            xMin = 0.3125F;
            xMax = 0.6875F;
            zMin = 0.3125F;
            zMax = 0.6875F;
        } else {
            xMin = 0.251F;
            xMax = 0.749F;
            zMin = 0.251F;
            zMax = 0.749F;
        }

        yMin = 0.5000F;
        yMax = yMin + (float) amount / te.getCapacity() / 4.016f;

        ms.pushPose();

        // render top only
        FluidRenderer.renderFluidStack(te.bloodTank.getFluid(), te, ms, bufferSource, xMin, xMax, yMin, yMax, zMin, zMax, true, true);

        ms.popPose();
    }

    protected void renderOther(BloodAltarTileEntity te, BloodAltarPhase phase, int tick, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        int brightness = LightTexture.FULL_BRIGHT;

        TextureAtlasSprite spriteFresh = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(AllTextures.FRESH);

        switch(phase) {
            case GROW, DECAY, NONE -> {}
            case BREED -> DrawUtils.renderSphereWithTexture(ms, te.getBreedSphereRadius(), spriteFresh, Color.RED, 240);
            case IMPACT_PRELUDE -> {
                DrawUtils.renderSphereWithTexture(ms, te.getBreedSphereRadius(), spriteFresh, Color.RED, 240);

                float s = (System.currentTimeMillis() % 1000) / 1000.0f;
                if (s > 0.5f) {
                    s = 1.0f - s;
                }
                float scale = 5.0f + s * 0.3f;

                TextureAtlasSprite spriteImpactCross = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(AllTextures.IMPACT_CROSS);

                ms.pushPose();

                VertexConsumer builder = bufferSource.getBuffer(RenderTypeLG.IMPACT);

                ms.translate(0.0, 20.0, 0.0);

                Quaternion rotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
                ms.mulPose(rotation);

                Matrix4f matrix = ms.last().pose();

                float alpha = Math.min((tick - BloodAltarTileEntity.PHASE_ONE_END) / 100.0f * 0.8f, 0.8f);

                // render cross
                builder.vertex(matrix, -scale, -scale, 0.0f).color(1.0f, 1.0f, 1.0f, alpha).uv(spriteImpactCross.getU1(), spriteImpactCross.getV1()).uv2(brightness).normal(1,0,0).endVertex();
                builder.vertex(matrix, -scale, scale, 0.0f).color(1.0f, 1.0f, 1.0f, alpha).uv(spriteImpactCross.getU1(), spriteImpactCross.getV0()).uv2(brightness).normal(1,0,0).endVertex();
                builder.vertex(matrix, scale, scale, 0.0f).color(1.0f, 1.0f, 1.0f, alpha).uv(spriteImpactCross.getU0(), spriteImpactCross.getV0()).uv2(brightness).normal(1,0,0).endVertex();
                builder.vertex(matrix, scale, -scale, 0.0f).color(1.0f, 1.0f, 1.0f, alpha).uv(spriteImpactCross.getU0(), spriteImpactCross.getV1()).uv2(brightness).normal(1,0,0).endVertex();

                ms.popPose();
            }
            case IMPACT_EMERGENCE -> {
                float s = (System.currentTimeMillis() % 1000) / 1000.0f;
                if (s > 0.5f) {
                    s = 1.0f - s;
                }
                float scale = 5.0f + s * 0.3f;

                TextureAtlasSprite spriteImpactCross = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(AllTextures.IMPACT_CROSS);
                TextureAtlasSprite spriteImpactRing = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(AllTextures.IMPACT_RING);

                ms.pushPose();

                VertexConsumer builder = bufferSource.getBuffer(RenderTypeLG.IMPACT);

                ms.translate(0.0, 20.0, 0.0);

                Quaternion rotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
                ms.mulPose(rotation);

                Matrix4f matrix = ms.last().pose();

                // render cross
                builder.vertex(matrix, -scale, -scale, 0.0f).color(1.0f, 1.0f, 1.0f, 0.8f).uv(spriteImpactCross.getU1(), spriteImpactCross.getV1()).uv2(brightness).normal(1,0,0).endVertex();
                builder.vertex(matrix, -scale, scale, 0.0f).color(1.0f, 1.0f, 1.0f, 0.8f).uv(spriteImpactCross.getU1(), spriteImpactCross.getV0()).uv2(brightness).normal(1,0,0).endVertex();
                builder.vertex(matrix, scale, scale, 0.0f).color(1.0f, 1.0f, 1.0f, 0.8f).uv(spriteImpactCross.getU0(), spriteImpactCross.getV0()).uv2(brightness).normal(1,0,0).endVertex();
                builder.vertex(matrix, scale, -scale, 0.0f).color(1.0f, 1.0f, 1.0f, 0.8f).uv(spriteImpactCross.getU0(), spriteImpactCross.getV1()).uv2(brightness).normal(1,0,0).endVertex();

                ms.popPose();

                // render rings

                ms.pushPose();

                int alpha1 = (int) (255 * Math.min((tick - BloodAltarTileEntity.PHASE_TWO_END) / 120.0f, 1.0f));
                int alpha2 = (int) (255 * Math.min((tick - 120 - BloodAltarTileEntity.PHASE_TWO_END) / 120.0f, 1.0f));
                int alpha3 = (int) (255 * Math.min((tick - 240 - BloodAltarTileEntity.PHASE_TWO_END) / 120.0f, 1.0f));

                long time = System.currentTimeMillis();

                float s1 = (time % 1000) / 1000.0f;
                if (s1 > 0.5f) {
                    s1 = 1.0f - s1;
                }

                float s2 = (time + 333 % 1000) / 1000.0f;
                if (s2 > 0.5f) {
                    s2 = 1.0f - s2;
                }

                float s3 = (time + 666 % 1000) / 1000.0f;
                if (s3 > 0.5f) {
                    s3 = 1.0f - s3;
                }

                float scaleRing1 = 4.0f + s1 * 0.2f;
                float scaleRing2 = 6.0f + s2 * 0.5f;
                float scaleRing3 = 10.0f + s3 * 0.8f;

                if (alpha1 > 0) {
                    DrawUtils.renderQuad(builder, ms, Vector3.ZERO.addY(12.5), Vector3.Y_NEG_AXIS, scaleRing1, spriteImpactRing, alpha1);
                    DrawUtils.renderQuad(builder, ms, Vector3.ZERO.addY(12.5), Vector3.Y_POS_AXIS, scaleRing1, spriteImpactRing, alpha1);
                }

                if (alpha2 > 0) {
                    DrawUtils.renderQuad(builder, ms, Vector3.ZERO.addY(15), Vector3.Y_NEG_AXIS, scaleRing2, spriteImpactRing, alpha2);
                    DrawUtils.renderQuad(builder, ms, Vector3.ZERO.addY(25), Vector3.Y_POS_AXIS, scaleRing2, spriteImpactRing, alpha2);
                }

                if (alpha3 > 0) {
                    DrawUtils.renderQuad(builder, ms, Vector3.ZERO.addY(17.5), Vector3.Y_NEG_AXIS, scaleRing3, spriteImpactRing, alpha3);
                    DrawUtils.renderQuad(builder, ms, Vector3.ZERO.addY(17.5), Vector3.Y_POS_AXIS, scaleRing3, spriteImpactRing, alpha3);
                }

                ms.popPose();

                int impactTick = tick - BloodAltarTileEntity.PHASE_TWO_END - BloodAltarTileEntity.IMPACT_DELAY;

                if (impactTick > 0) {
                    if (impactTick < 60) {
                        DrawUtils.renderSphereWithTexture(ms, impactTick / 60.0 * BloodAltarTileEntity.IMPACT_RADIUS_MIN, spriteFresh, Color.RED, 240);
                    } else if (impactTick < 120) {
                        DrawUtils.renderSphereWithTexture(ms, (impactTick - 60) / 60.0 * BloodAltarTileEntity.IMPACT_RADIUS_MED, spriteFresh, Color.RED, 240);
                    } else if (impactTick < 240) {
                        DrawUtils.renderSphereWithTexture(ms, (impactTick - 120) / 120.0 * BloodAltarTileEntity.IMPACT_RADIUS_MAX, spriteFresh, Color.RED, 240);
                    }
                }

            }
        }
    }
}
