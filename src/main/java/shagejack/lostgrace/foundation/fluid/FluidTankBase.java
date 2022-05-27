package shagejack.lostgrace.foundation.fluid;

import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import shagejack.lostgrace.foundation.network.AllPackets;
import shagejack.lostgrace.foundation.network.packet.FluidUpdatePacket;
import shagejack.lostgrace.foundation.tile.SyncedTileEntity;

import java.util.function.Predicate;

public class FluidTankBase<T extends SyncedTileEntity> extends FluidTank {

    protected T parent;

    public FluidTankBase(T parent, int capacity) {
        super(capacity);
        this.parent = parent;
    }

    public FluidTankBase(T parent, int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
        this.parent = parent;
    }

    @Override
    protected void onContentsChanged() {
        if (parent instanceof IFluidTankUpdater) {
            ((IFluidTankUpdater) parent).onTankContentsChanged();
        }

        parent.setChanged();
        Level level = parent.getLevel();
        if(level != null && !level.isClientSide()) {
            AllPackets.sendToNear(level, parent.getBlockPos(), new FluidUpdatePacket(parent.getBlockPos(), this.getFluid()));
        }
    }

    public void empty() {
        this.setFluid(FluidStack.EMPTY);
    }

    public boolean isFull() {
        return !this.isEmpty() && this.getFluidAmount() >= this.getCapacity();
    }
}
