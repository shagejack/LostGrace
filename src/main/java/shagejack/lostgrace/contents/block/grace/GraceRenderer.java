package shagejack.lostgrace.contents.block.grace;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Quaternion;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import shagejack.lostgrace.foundation.render.DrawUtils;
import shagejack.lostgrace.foundation.render.RenderTypeLG;
import shagejack.lostgrace.foundation.tile.renderer.SafeTileEntityRenderer;
import shagejack.lostgrace.foundation.utility.Color;
import shagejack.lostgrace.registries.AllTextures;

public class GraceRenderer extends SafeTileEntityRenderer<GraceTileEntity> {

    public GraceRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(GraceTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderGrace(te, partialTicks, ms, buffer, light, overlay);
    }

    protected void renderGrace(GraceTileEntity grace, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (grace.getSummonRemainingTicks() > 0) {
            float treeScale = 5.0f;

            // TODO: render golden tree
            // ms.pushPose();

            // ms.popPose();
        }

        float time = (System.currentTimeMillis() % 4000) / 4000.0f;
        float s = time;
        if (s > 0.5f) {
            s = 1.0f - s;
        }
        float scale = 1.0f + s * 0.2f;
        int alpha = (int) ((0.8 + 0.2 * Math.sin(time * Math.PI)) * 255);

        boolean isTableGrace = grace.isTableGrace();

        TextureAtlasSprite spriteHumanity = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(AllTextures.HUMANITY);

        ms.pushPose();

        if (isTableGrace) {
            scale *= 2;
            ms.translate(0.5, 1.0, 0.5);
        } else {
            ms.translate(0.5, 0.75, 0.5);
        }

        // TODO: render rework

        VertexConsumer buffer = bufferSource.getBuffer(RenderTypeLG.HUMANITY);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Quaternion rotation = camera.rotation();

        ms.pushPose();
        ms.mulPose(rotation);

        DrawUtils.renderSimpleIcon(buffer, ms, spriteHumanity, Color.WHITE, alpha, scale);

        ms.popPose();

        ms.popPose();

        // render particles

        /*
        if (Minecraft.getInstance().isPaused())
            return;

        if (Minecraft.getInstance().player == null)
            return;

        if (isTableGrace)
            return;

        Vector3 tilePos = grace.getCenterPos().addY(0.25);
        double distance = Vector3.of(Minecraft.getInstance().player).distance(tilePos);

        if (distance < Constants.GRACE_FORCE_FIRST_PERSON_DISTANCE || distance > 64)
            return;

        long t = System.currentTimeMillis();
        double s1 = 0.1 + t % 7600 / 4000.0;
        Random dRandom = new Random(t / 7600 + grace.getGrace().hashCode());
        Level level = Objects.requireNonNull(grace.getLevel());

        int count = dRandom.nextInt(1, 4);

        if (level.getRandom().nextDouble() < 0.1) {
            for (int i = 0; i < count; i++) {
                Vector3 destination = new Vector3((3 + dRandom.nextDouble() * 5) * (dRandom.nextBoolean() ? 1 : -1), 1.5 + dRandom.nextDouble() * 3, (3 + dRandom.nextDouble() * 5) * (dRandom.nextBoolean() ? 1 : -1));
                Vector3 pPos = Vector3.ZERO.quadraticInterpolation(destination, s1);
                Vector3 velocity = Vector3.ZERO.quadraticInterpolation(destination, s1 + 0.01).subtract(pPos).normalize().multiply(0.025);
                level.addParticle(ParticleTypes.SMALL_FLAME, tilePos.x() + pPos.x(), tilePos.y() + pPos.y(), tilePos.z() + pPos.z(), velocity.x(), velocity.y(), velocity.z());
            }
        }

         */
    }

}
