package shagejack.lostgrace.contents.block.grace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import shagejack.lostgrace.contents.grace.IGraceHandler;

public class GraceUI {

    private final Level level;
    private final BlockPos pos;
    private final double sphereRadius;
    private final IGraceHandler graceHandler;

    private int renderTicks = 20;
    private int teleportTicks = 0;

    private GraceUI(Level level, BlockPos pos, double sphereRadius, IGraceHandler graceHandler) {
        this.level = level;
        this.pos = pos;
        this.sphereRadius = sphereRadius;
        this.graceHandler = graceHandler;
    }

    public static GraceUI create(Level level, BlockPos graceTilePos, double sphereRadius, IGraceHandler graceHandler) {
        return new GraceUI(level, graceTilePos, sphereRadius, graceHandler);
    }

    public IGraceHandler getGraceHandler() {
        return graceHandler;
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public double getSphereRadius() {
        return sphereRadius;
    }

    public int getRenderTicks() {
        return renderTicks;
    }

    public int getTeleportTicks() {
        return teleportTicks;
    }

    public void refresh() {
        this.renderTicks = 20;
        this.teleportTicks = 0;
    }

    public void resetTeleportTicks() {
        this.teleportTicks = 0;
    }

    public void decreaseRenderTicks() {
        this.renderTicks--;
    }

    public void increaseTeleportTicks() {
        this.teleportTicks++;
    }





}
