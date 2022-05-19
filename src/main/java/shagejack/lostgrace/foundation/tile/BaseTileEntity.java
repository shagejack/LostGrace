package shagejack.lostgrace.foundation.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseTileEntity extends SyncedTileEntity implements IPartialSafeNBT{
    private boolean initialized = false;
    private boolean firstNbtRead = true;
    private int lazyTickRate;
    private int lazyTickCounter;

    public BaseTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(10);
    }

    public void onRemoved() {

    }

    public void tick() {
        if (!initialized && hasLevel()) {
            initialize();
            initialized = true;
        }

        if (lazyTickCounter-- <= 0) {
            lazyTickCounter = lazyTickRate;
            lazyTick();
        }
    }

    public void lazyTick() {

    }

    public void setLazyTickRate(int slowTickRate) {
        this.lazyTickRate = slowTickRate;
        this.lazyTickCounter = slowTickRate;
    }

    public void initialize() {
        if (firstNbtRead) {
            firstNbtRead = false;
        }

        lazyTick();
    }

    /**
     * Hook only these in future subclasses of STE
     */
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.saveAdditional(tag);
    }

    @Override
    public void writeSafe(CompoundTag tag, boolean clientPacket) {
        super.saveAdditional(tag);
    }

    /**
     * Hook only these in future subclasses of STE
     */
    protected void read(CompoundTag tag, boolean clientPacket) {
        if (firstNbtRead) {
            firstNbtRead = false;
        }
        super.load(tag);
    }

    @Override
    public final void load(CompoundTag tag) {
        read(tag, false);
    }

    @Override
    public final void saveAdditional(CompoundTag tag) {
        write(tag, false);
    }

    @Override
    public final void readClient(CompoundTag tag) {
        read(tag, true);
    }

    @Override
    public final CompoundTag writeClient(CompoundTag tag) {
        write(tag, true);
        return tag;
    }
}
