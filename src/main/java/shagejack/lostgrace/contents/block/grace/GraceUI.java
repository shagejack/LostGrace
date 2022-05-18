package shagejack.lostgrace.contents.block.grace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import shagejack.lostgrace.contents.grace.IGraceHandler;

public class GraceUI {

    private final Level level;
    private final BlockPos pos;
    private final IGraceHandler graceHandler;

    private int renderTicks = 20;
    private int teleportTicks = 0;

    private GraceUI(Level level, BlockPos pos, IGraceHandler graceHandler) {
        this.level = level;
        this.pos = pos;
        this.graceHandler = graceHandler;
    }

    public static GraceUI create(Level level, BlockPos graceTilePos, IGraceHandler graceHandler) {
        return new GraceUI(level, graceTilePos, graceHandler);
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
