package shagejack.lostgrace.contents.block.bloodAltar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shagejack.lostgrace.foundation.fluid.FluidTankBase;
import shagejack.lostgrace.foundation.fluid.ITankTileEntity;
import shagejack.lostgrace.foundation.network.packet.FluidUpdatePacket;
import shagejack.lostgrace.foundation.tile.BaseTileEntity;
import shagejack.lostgrace.registries.tile.AllTileEntities;

public class BloodAltarTileEntity extends BaseTileEntity implements ITankTileEntity, FluidUpdatePacket.IFluidPacketReceiver{

    public FluidTankBase<BloodAltarTileEntity> bloodTank;

    LazyOptional<IFluidHandler> tankHandlerLazyOptional;

    public BloodAltarTileEntity(BlockPos pos, BlockState state) {
        super(AllTileEntities.bloodAltar.get(), pos, state);
        this.bloodTank = new FluidTankBase<>(this, 500);
        this.tankHandlerLazyOptional = LazyOptional.of(() -> bloodTank);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        bloodTank.writeToNBT(tag);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        bloodTank.readFromNBT(tag);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != null && side.getAxis() == Direction.Axis.Y) {
            return tankHandlerLazyOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        tankHandlerLazyOptional.invalidate();
    }

    @Override
    public int getCapacity() {
        return 500;
    }

    @Override
    public void updateFluidTo(FluidStack fluid) {
        this.bloodTank.setFluid(fluid);
    }

    public boolean isEmpty() {
        return this.bloodTank.isEmpty();
    }

    public void tryStart() {

    }
}
