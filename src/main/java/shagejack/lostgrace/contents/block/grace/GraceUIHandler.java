package shagejack.lostgrace.contents.block.grace;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.contents.grace.Grace;
import shagejack.lostgrace.contents.grace.GraceProvider;
import shagejack.lostgrace.contents.grace.IGraceHandler;
import shagejack.lostgrace.foundation.network.AllPackets;
import shagejack.lostgrace.foundation.network.packet.TeleportGracePacket;
import shagejack.lostgrace.foundation.render.RenderTypeLG;
import shagejack.lostgrace.foundation.render.SphereBuilder;
import shagejack.lostgrace.foundation.utility.ITickHandler;
import shagejack.lostgrace.foundation.utility.TileEntityUtils;
import shagejack.lostgrace.foundation.utility.Vector3;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class GraceUIHandler implements ITickHandler {

    public static final ResourceLocation HUMANITY = LostGrace.asResource("block/grace/humanity");

    private static GraceUIHandler INSTANCE = new GraceUIHandler();

    private final List<SphereBuilder.TriangleFace> sphereFaces = new SphereBuilder().build(6, 16, false);

    private GraceUI currentUI = null;

    // there should be only one grace ui render handler instance on client side
    private GraceUIHandler() {}

    public static GraceUIHandler getInstance() {
        return INSTANCE;
    }

    public GraceUI getOrCreateUI(Level level, BlockPos graceTilePos, IGraceHandler graceHandler) {
        if (currentUI == null || !currentUI.getLevel().dimension().location().equals(level.dimension().location()) || !currentUI.getPos().equals(graceTilePos)) {
            currentUI = GraceUI.create(level, graceTilePos, 5.5D, graceHandler);
        }

        return currentUI;
    }

    public GraceUI getCurrentUI() {
        return currentUI;
    }

    private boolean validate() {
        if (this.currentUI == null) {
            return true;
        }

        Level level = Minecraft.getInstance().level;

        if (level == null || !this.currentUI.getLevel().dimension().location().equals(level.dimension().location())) {
            this.currentUI.refresh();
            this.currentUI = null;
            return true;
        }

        Player player = Minecraft.getInstance().player;

        if (player == null) {
            this.currentUI.refresh();
            this.currentUI = null;
            return true;
        }

        LazyOptional<IGraceHandler> graceHandler = player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY);

        if (!graceHandler.isPresent() || graceHandler.resolve().isEmpty() || !graceHandler.resolve().get().isGraceActivated()) {
            this.currentUI.refresh();
            this.currentUI = null;
            return true;
        }

        Optional<GraceTileEntity> te = TileEntityUtils.get(GraceTileEntity.class, level, this.currentUI.getPos(), true);

        if (te.isEmpty() || !te.get().shouldRenderFog()) {
            this.currentUI.refresh();
            this.currentUI = null;
            return true;
        }

        return this.currentUI == null;
    }

    public void render(RenderLevelLastEvent event) {
        if (this.validate())
            return;

        float pTick = event.getPartialTick();
        PoseStack renderStack = event.getPoseStack();
        IGraceHandler graceHandler = this.currentUI.getGraceHandler();

        Player player = Minecraft.getInstance().player;
        
        if (player == null)
            return;

        Vector3 renderOffset = Vector3.of(player).addY(1.7);
        double distance = renderOffset.distance(Vector3.of(this.currentUI.getPos()).add(0.5, 1.8, 0.5));

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        renderStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        if(distance > 3) {
            return;
        }

        this.renderFog(renderStack, renderOffset, pTick);
        this.renderGraces(renderStack, renderOffset, pTick, graceHandler);
    }

    private void renderFog(PoseStack renderStack, Vector3 renderOffset, float pTick) {
        renderStack.pushPose();

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderTypeLG.FOG_SPHERE);

        Color color = Color.LIGHT_GRAY;
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int alpha;

        Matrix4f renderMatrix = renderStack.last().pose();

        if (this.currentUI.getTeleportTicks() > 0) {
            alpha = Math.max((int) (255 * Math.min(1, (double) this.currentUI.getTeleportTicks() / 20)), (int) (217 * (1 - (double) this.currentUI.getRenderTicks() / 20)));
            List<SphereBuilder.TriangleFace> generatedSphereFaces = new SphereBuilder().build(6 * Math.max(0.25, 1 - (double) this.currentUI.getTeleportTicks() / 80), 16, false);
            for (SphereBuilder.TriangleFace face : generatedSphereFaces) {
                renderOffset.add(face.getV1()).drawPosVertex(renderMatrix, vertexConsumer).color(r, g, b, alpha).endVertex();
                renderOffset.add(face.getV2()).drawPosVertex(renderMatrix, vertexConsumer).color(r, g, b, alpha).endVertex();
                renderOffset.add(face.getV3()).drawPosVertex(renderMatrix, vertexConsumer).color(r, g, b, alpha).endVertex();
            }
        } else {
            alpha = (int) (217 * (1 - (double) this.currentUI.getRenderTicks() / 20));
            for (SphereBuilder.TriangleFace face : this.sphereFaces) {
                renderOffset.add(face.getV1()).drawPosVertex(renderMatrix, vertexConsumer).color(r, g, b, alpha).endVertex();
                renderOffset.add(face.getV2()).drawPosVertex(renderMatrix, vertexConsumer).color(r, g, b, alpha).endVertex();
                renderOffset.add(face.getV3()).drawPosVertex(renderMatrix, vertexConsumer).color(r, g, b, alpha).endVertex();
            }
        }

        RenderSystem.depthMask(true);
        buffer.endBatch(RenderTypeLG.FOG_SPHERE);

        renderStack.popPose();
    }

    private void renderGraces(PoseStack renderStack, Vector3 renderOffset, float pTick, IGraceHandler graceHandler) {
        int brightness = LightTexture.FULL_BRIGHT;
        TextureAtlasSprite spriteHumanity = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(HUMANITY);

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderTypeLG.GRACE);
        List<Grace> graces = graceHandler.getAllGracesFound();

        for (Grace grace : graces) {
            Vector3 pos = Vector3.of(grace.getPos()).add(0.5, 0.5, 0.5);
            if (pos.addY(1.1).distance(renderOffset) > 5.5D || !grace.getLevel().dimension().location().equals(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.dimension().location() : null)) {
                Vector3 graceVector = pos.subtract(renderOffset).normalize().multiply(5);

                Player player = Minecraft.getInstance().player;

                if (player == null)
                    return;

                if (Vector3.of(player.getViewVector(pTick)).includedAngleDegree(graceVector) < 5) {
                    Font font = Minecraft.getInstance().font;

                    float s = (System.currentTimeMillis() % 10000) / 10000.0f;
                    if (s > 0.5f) {
                        s = 1.0f - s;
                    }
                    float scale = 0.4f + s * 0.08f;

                    Vector3 rightRenderVector = Vector3.Z_AXIS.multiply(5);

                    renderStack.pushPose();

                    renderStack.mulPose(graceVector.asToVecRotation(rightRenderVector));

                    Matrix4f renderMatrix = renderStack.last().pose();

                    Vector3 renderPos = renderOffset.add(rightRenderVector);

                    renderPos.add(-scale, -scale, 0).drawPosVertex(renderMatrix, vertexConsumer).color(1.0f, 1.0f, 1.0f, 1.0f).uv(spriteHumanity.getU0(), spriteHumanity.getV0()).uv2(brightness).normal(1, 0, 0).endVertex();
                    renderPos.add(-scale, scale, 0).drawPosVertex(renderMatrix, vertexConsumer).color(1.0f, 1.0f, 1.0f, 1.0f).uv(spriteHumanity.getU0(), spriteHumanity.getV1()).uv2(brightness).normal(1, 0, 0).endVertex();
                    renderPos.add(scale, scale, 0).drawPosVertex(renderMatrix, vertexConsumer).color(1.0f, 1.0f, 1.0f, 1.0f).uv(spriteHumanity.getU1(), spriteHumanity.getV1()).uv2(brightness).normal(1, 0, 0).endVertex();
                    renderPos.add(scale, -scale, 0).drawPosVertex(renderMatrix, vertexConsumer).color(1.0f, 1.0f, 1.0f, 1.0f).uv(spriteHumanity.getU1(), spriteHumanity.getV0()).uv2(brightness).normal(1, 0, 0).endVertex();

                    if (grace.hasName()) {
                        String text = grace.getName();
                        renderStack.translate(renderPos.x(), renderPos.y(), renderPos.z());
                        font.drawShadow(renderStack, text, font.width(text), font.lineHeight, Color.ORANGE.getRGB());
                    }

                    renderStack.popPose();
                } else {
                    float s = (System.currentTimeMillis() % 10000) / 10000.0f;
                    if (s > 0.5f) {
                        s = 1.0f - s;
                    }
                    float scale = 0.25f + s * 0.05f;

                    Vector3 rightRenderVector = Vector3.Z_AXIS.multiply(5);

                    renderStack.pushPose();

                    renderStack.mulPose(graceVector.asToVecRotation(rightRenderVector));

                    Matrix4f renderMatrix = renderStack.last().pose();

                    renderOffset.add(rightRenderVector).add(-scale, -scale, 0).drawPosVertex(renderMatrix, vertexConsumer).color(1.0f, 1.0f, 1.0f, 0.6f).uv(spriteHumanity.getU0(), spriteHumanity.getV0()).uv2(brightness).normal(1, 0, 0).endVertex();
                    renderOffset.add(rightRenderVector).add(-scale, scale, 0).drawPosVertex(renderMatrix, vertexConsumer).color(1.0f, 1.0f, 1.0f, 0.6f).uv(spriteHumanity.getU0(), spriteHumanity.getV1()).uv2(brightness).normal(1, 0, 0).endVertex();
                    renderOffset.add(rightRenderVector).add(scale, scale, 0).drawPosVertex(renderMatrix, vertexConsumer).color(1.0f, 1.0f, 1.0f, 0.6f).uv(spriteHumanity.getU1(), spriteHumanity.getV1()).uv2(brightness).normal(1, 0, 0).endVertex();
                    renderOffset.add(rightRenderVector).add(scale, -scale, 0).drawPosVertex(renderMatrix, vertexConsumer).color(1.0f, 1.0f, 1.0f, 0.6f).uv(spriteHumanity.getU1(), spriteHumanity.getV0()).uv2(brightness).normal(1, 0, 0).endVertex();

                    renderStack.popPose();
                }

            }

        }


        RenderSystem.depthMask(true);
        buffer.endBatch(RenderTypeLG.GRACE);
    }

    public void interact(TickEvent.ClientTickEvent event) {
        if (this.validate())
            return;

        IGraceHandler graceHandler = this.currentUI.getGraceHandler();

        Player player = Minecraft.getInstance().player;

        if (player == null) {
            this.currentUI.resetTeleportTicks();
            return;
        }

        if (!player.isShiftKeyDown()) {
            this.currentUI.resetTeleportTicks();
            return;
        }

        Vector3 playerPos = Vector3.of(player);
        List<Grace> graces = graceHandler.getAllGracesFound().stream().filter(grace -> {
            Vector3 pos = Vector3.of(grace.getPos()).add(0.5, 0.5, 0.5);
            if (pos.addY(1.1).distance(playerPos) > 5.5D || !grace.getLevel().dimension().location().equals(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.dimension().location() : null)) {
                Vector3 graceVector = pos.subtract(playerPos).normalize();
                return Vector3.of(player.getViewVector(1.0F)).includedAngleDegree(graceVector) < 5;
            }
            return false;
        }).toList();

        if (!graces.isEmpty()) {
            if (this.currentUI.getTeleportTicks() < 60) {
                this.currentUI.increaseTeleportTicks();
            } else {
                AllPackets.sendToServer(new TeleportGracePacket(player.getUUID(), graces.get(0)));
                this.currentUI.resetTeleportTicks();
                validate();
            }
        } else {
            this.currentUI.resetTeleportTicks();
        }
    }

    @Override
    public String getName() {
        return "GraceUI Render Handler";
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if (this.currentUI != null) {
            if (this.currentUI.getRenderTicks() > 0)
                this.currentUI.decreaseRenderTicks();
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.CLIENT);
    }

    @Override
    public boolean shouldFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }
}
