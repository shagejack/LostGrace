package shagejack.lostgrace.contents.block.grace;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
import shagejack.lostgrace.foundation.render.DrawUtils;
import shagejack.lostgrace.foundation.render.RenderTypeLG;
import shagejack.lostgrace.foundation.render.SphereBuilder;
import shagejack.lostgrace.foundation.render.TriangleFace;
import shagejack.lostgrace.foundation.utility.Constants;
import shagejack.lostgrace.foundation.utility.ITickHandler;
import shagejack.lostgrace.foundation.utility.TileEntityUtils;
import shagejack.lostgrace.foundation.utility.Vector3;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class GraceUIHandler implements ITickHandler {

    public static final ResourceLocation HUMANITY = LostGrace.asResource("block/grace/humanity");

    private static final GraceUIHandler INSTANCE = new GraceUIHandler();

    private static final List<TriangleFace> sphereFaces = new SphereBuilder().build(Constants.GRACE_FOG_RADIUS, 16, false);

    private GraceUI currentUI = null;
    private int fadeTick = 0;

    // there should be only one grace ui render handler instance on client side
    private GraceUIHandler() {}

    public static GraceUIHandler getInstance() {
        return INSTANCE;
    }

    public GraceUI getOrCreateUI(Level level, BlockPos graceTilePos, IGraceHandler graceHandler) {
        if (currentUI == null || !currentUI.getLevel().dimension().location().equals(level.dimension().location()) || !currentUI.getPos().equals(graceTilePos)) {
            currentUI = GraceUI.create(level, graceTilePos, graceHandler);
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
        float pTick = event.getPartialTick();
        PoseStack renderStack = event.getPoseStack();

        Player player = Minecraft.getInstance().player;

        if (player == null)
            return;

        Vector3 renderOffset = Vector3.of(player).addY(Constants.PLAYER_SIGHT_Y_OFFSET);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        renderStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        if (this.validate()) {
            if (fadeTick > 0) {
                this.renderFog(renderStack, renderOffset, pTick, true);
            }
            return;
        }

        IGraceHandler graceHandler = this.currentUI.getGraceHandler();
        double distance = Vector3.of(player).distance(Vector3.of(this.currentUI.getPos()).add(0.5, Constants.GRACE_DISTANCE_Y_OFFSET, 0.5));

        if(distance > 3) {
            if (fadeTick > 0) {
                this.renderFog(renderStack, renderOffset, pTick, true);
            }
            return;
        }

        this.renderFog(renderStack, renderOffset, pTick, false);
        this.renderGraces(renderStack, renderOffset, pTick, graceHandler);
    }

    private void renderFog(PoseStack renderStack, Vector3 renderOffset, float pTick, boolean fading) {
        renderStack.pushPose();

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderTypeLG.SPHERE);

        Color color = Constants.GRACE_FOG_COLOR;

        int alpha;

        Matrix4f renderMatrix = renderStack.last().pose();

        if (!fading) {
            this.fadeTick = 20 - this.currentUI.getRenderTicks();

            if (this.currentUI.getTeleportTicks() > 0) {
                alpha = (int) ((255 - Constants.GRACE_FOG_ALPHA) * Math.min(1, (double) this.currentUI.getTeleportTicks() / 60) + Constants.GRACE_FOG_ALPHA * (1 - (double) this.currentUI.getRenderTicks() / 20));
                List<TriangleFace> generatedSphereFaces = new SphereBuilder().build(Constants.GRACE_FOG_RADIUS * Math.max(0.25, 1 - (double) this.currentUI.getTeleportTicks() / 80), 16, false);
                for (TriangleFace face : generatedSphereFaces) {
                    DrawUtils.renderTriangleWithColor(vertexConsumer, renderStack, renderOffset, face, color, alpha);
                }
            } else {
                alpha = (int) (Constants.GRACE_FOG_ALPHA * (1 - (double) this.currentUI.getRenderTicks() / 20));
                for (TriangleFace face : sphereFaces) {
                    DrawUtils.renderTriangleWithColor(vertexConsumer, renderStack, renderOffset, face, color, alpha);
                }
            }
        } else {
            alpha = (int) (Constants.GRACE_FOG_ALPHA * ((double) this.fadeTick / 20));
            for (TriangleFace face : sphereFaces) {
                DrawUtils.renderTriangleWithColor(vertexConsumer, renderStack, renderOffset, face, color, alpha);
            }
        }

        buffer.endBatch(RenderTypeLG.SPHERE);

        renderStack.popPose();
    }
    
    private void renderGraces(PoseStack renderStack, Vector3 renderOffset, float pTick, IGraceHandler graceHandler) {
        TextureAtlasSprite spriteHumanity = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(HUMANITY);

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderTypeLG.GRACE);
        List<Grace> graces = graceHandler.getAllGracesFound();

        for (Grace grace : graces) {
            Vector3 pos = Vector3.of(grace.getPos()).add(0.5, 0.5, 0.5);
            if (pos.addY(Constants.GRACE_DISTANCE_Y_OFFSET - 0.5).distance(renderOffset) > Constants.GRACE_FORCE_FIRST_PERSON_DISTANCE || !grace.getLevel().dimension().location().equals(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.dimension().location() : null)) {
                Vector3 graceVector = pos.subtract(renderOffset).normalize().multiply(5);

                Player player = Minecraft.getInstance().player;

                if (player == null)
                    return;

                if (Vector3.of(player.getViewVector(pTick)).includedAngleDegree(graceVector) < Constants.GRACE_TELEPORT_SELECTION_DEVIATION_DEGREE) {
                    // render focused grace
                    float s = (System.currentTimeMillis() % 10000) / 10000.0f;
                    if (s > 0.5f) {
                        s = 1.0f - s;
                    }
                    float scale = 0.4f + s * 0.08f;

                    scale *= (1 + 4 * currentUI.getTeleportTicks() / 60.0);

                    Vector3 renderPos = renderOffset.add(graceVector.multiply(1 - currentUI.getTeleportTicks() / 60.0));

                    DrawUtils.renderQuad(vertexConsumer, renderStack, renderPos, graceVector.opposite().normalize(), scale, spriteHumanity, 255);

                    Color textColor = new Color(255, 215, 0);

                    // FIXME: 2022/5/19 fix text rendering
                    if (grace.hasName()) {
                        DrawUtils.renderInLevelText(renderStack, renderPos.addY(0.2), grace.getName(), textColor, scale);
                    }
                } else {
                    // render distant graces
                    float s = (System.currentTimeMillis() % 10000) / 10000.0f;
                    if (s > 0.5f) {
                        s = 1.0f - s;
                    }
                    float scale = 0.25f + s * 0.05f;

                    Vector3 renderPos = renderOffset.add(graceVector);

                    DrawUtils.renderQuad(vertexConsumer, renderStack, renderPos, graceVector.opposite().normalize(), scale, spriteHumanity, 127);

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
            // interact with distant grace
            if (pos.addY(Constants.GRACE_DISTANCE_Y_OFFSET - 0.5).distance(playerPos) > Constants.GRACE_FORCE_FIRST_PERSON_DISTANCE || !grace.getLevel().dimension().location().equals(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.dimension().location() : null)) {
                Vector3 graceVector = pos.subtract(playerPos.addY(Constants.PLAYER_SIGHT_Y_OFFSET)).normalize();
                return Vector3.of(player.getViewVector(1.0F)).includedAngleDegree(graceVector) < Constants.GRACE_TELEPORT_SELECTION_DEVIATION_DEGREE;
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
            if (this.currentUI.getRenderTicks() > 0) {
                this.currentUI.decreaseRenderTicks();
            }

            Player player = Minecraft.getInstance().player;

            if (player == null)
                return;

            double distance = Vector3.of(player).distance(Vector3.of(this.currentUI.getPos()).add(0.5, Constants.GRACE_DISTANCE_Y_OFFSET, 0.5));

        } else if (fadeTick > 0) {
            this.fadeTick--;
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
