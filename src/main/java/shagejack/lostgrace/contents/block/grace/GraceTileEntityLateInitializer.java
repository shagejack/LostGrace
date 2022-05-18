package shagejack.lostgrace.contents.block.grace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import shagejack.lostgrace.contents.block.grace.GraceTileEntity;
import shagejack.lostgrace.foundation.tile.TileEntityLateInitializer;

import java.util.function.Consumer;

public class GraceTileEntityLateInitializer implements TileEntityLateInitializer<GraceTileEntity> {
    private int life;
    private boolean initialized;

    private final Level level;
    private final BlockPos pos;
    private final Consumer<GraceTileEntity> action;

    public GraceTileEntityLateInitializer(Level level, BlockPos pos, Consumer<GraceTileEntity> action, int life) {
        this.life = life;
        this.initialized = false;
        this.level = level;
        this.pos = pos;
        this.action = action;
    }

    public GraceTileEntityLateInitializer(Level level, BlockPos pos, Consumer<GraceTileEntity> action) {
        this.life = 40;
        this.initialized = false;
        this.level = level;
        this.pos = pos;
        this.action = action;
    }

    @Override
    public int getLife() {
        return life;
    }

    @Override
    public void decreaseLife() {
        if (life > 0)
            life--;
    }

    @Override
    public boolean initialized() {
        return initialized;
    }

    @Override
    public void setInitialized() {
        this.initialized = true;
    }

    @Override
    public Class<GraceTileEntity> getTileClass() {
        return GraceTileEntity.class;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public BlockPos getTilePos() {
        return pos;
    }

    @Override
    public Consumer<GraceTileEntity> getAction() {
        return action;
    }
}
