package shagejack.lostgrace.contents.block.grace;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import shagejack.lostgrace.contents.grace.GraceProvider;
import shagejack.lostgrace.contents.grace.IGraceHandler;
import shagejack.lostgrace.foundation.render.Blending;
import shagejack.lostgrace.foundation.render.RenderTypeLG;
import shagejack.lostgrace.foundation.render.RenderingUtils;
import shagejack.lostgrace.foundation.render.SphereBuilder;
import shagejack.lostgrace.foundation.utility.ITickHandler;
import shagejack.lostgrace.foundation.utility.TileEntityUtils;
import shagejack.lostgrace.foundation.utility.Vector3;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class GraceUIRenderHandler implements ITickHandler {

    private static GraceUIRenderHandler INSTANCE = new GraceUIRenderHandler();

    private final List<SphereBuilder.TriangleFace> sphereFaces = new SphereBuilder().build(Vector3.Y_AXIS.multiply(6), 8, 10);

    private GraceUI currentUI = null;

    // there should be only one grace ui render handler instance on client side
    private GraceUIRenderHandler() {}

    public static GraceUIRenderHandler getInstance() {
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

        if (player == null ) {
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

        Vector3 renderOffset = Vector3.of(Minecraft.getInstance().player.position());
        double distance = renderOffset.distance(Vector3.of(this.currentUI.getPos()).add(0.5, 1.8, 0.5));

        if(distance > 3) {
            return;
        }

        this.renderFog(renderStack, renderOffset, pTick);
        this.renderGraces(renderStack, renderOffset, pTick, graceHandler);
        this.renderFocusedGrace(renderStack, renderOffset, pTick, graceHandler);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderFog(PoseStack renderStack, Vector3 renderOffset, float pTick) {

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderTypeLG.FOG_SPHERE);

        renderStack.pushPose();

        Color color = Color.BLACK;
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int alpha = (int) (255 * (1 - (double) this.currentUI.getRenderTicks() / 20));

        Matrix4f renderMatrix = renderStack.last().pose();

        // TODO: fix rendering, try fix sphere triangles generation in SphereBuilder
        for (SphereBuilder.TriangleFace face : this.sphereFaces) {
            renderOffset.add(face.getV1()).drawPosVertex(renderMatrix, vertexConsumer).color(r, g, b, alpha).endVertex();
            renderOffset.add(face.getV2()).drawPosVertex(renderMatrix, vertexConsumer).color(r, g, b, alpha).endVertex();
            renderOffset.add(face.getV3()).drawPosVertex(renderMatrix, vertexConsumer).color(r, g, b, alpha).endVertex();
        }

        renderStack.popPose();

        RenderSystem.depthMask(true);
        buffer.endBatch(RenderTypeLG.FOG_SPHERE);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderFocusedGrace(PoseStack renderStack, Vector3 renderOffset, float pTick, IGraceHandler graceHandler) {
    }

    @OnlyIn(Dist.CLIENT)
    private void renderGraces(PoseStack renderStack, Vector3 renderOffset, float pTick, IGraceHandler graceHandler) {
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
