package shagejack.lostgrace.contents.block.dreamPool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
import shagejack.lostgrace.foundation.utility.Constants;
import shagejack.lostgrace.foundation.utility.DropUtils;
import shagejack.lostgrace.foundation.utility.Vector3;
import shagejack.lostgrace.registries.fluid.AllFluids;
import shagejack.lostgrace.registries.item.AllItems;
import shagejack.lostgrace.registries.tile.AllTileEntities;

public class DreamPoolTileEntity extends BaseTileEntity implements ITankTileEntity, FluidUpdatePacket.IFluidPacketReceiver {

    public FluidTankBase<DreamPoolTileEntity> tank;

    LazyOptional<IFluidHandler> tankHandlerLazyOptional;

    private int processedDreamAmount;
    private int processTick;

    public DreamPoolTileEntity(BlockPos pos, BlockState state) {
        super(AllTileEntities.dreamPool.get(), pos, state);
        this.tank = new FluidTankBase<>(this, 1000, fluid -> fluid.getFluid().isSame(AllFluids.dream.asFluid()));
        this.tankHandlerLazyOptional = LazyOptional.of(() -> tank);
        this.processedDreamAmount = 0;
        this.processTick = Constants.TICK_PER_THOUSANDTH;
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null)
            return;

        if (processedDreamAmount >= 1000) {
            DropUtils.dropItemStack(level, getBlockPos(), new ItemStack(AllItems.brokenDream.get()));
            this.processedDreamAmount -= 1000;
        }

        if (!tank.isEmpty()) {

            level.getEntitiesOfClass(LivingEntity.class, new AABB(getBlockPos()).inflate(getDreamRadius())).stream()
                        .filter(entity -> Vector3.of(entity).distance(Vector3.atCenterOf(getBlockPos())) <= getDreamRadius())
                        .forEach(entity -> entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * getAmount() / 100, getAmount() / 200)));

            if (processTick > 1) {
                this.processTick--;
            } else {
                this.processTick = Constants.TICK_PER_THOUSANDTH;
                FluidStack consume = tank.drain(1, IFluidHandler.FluidAction.EXECUTE);
                if (!consume.isEmpty()) {
                    this.processedDreamAmount++;
                }
            }
        } else {
            this.processTick = Constants.TICK_PER_THOUSANDTH;
            this.processedDreamAmount--;
        }
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        this.tank.writeToNBT(tag);
        tag.putInt("ProcessedDreamAmount", processedDreamAmount);
        tag.putInt("ProcessTick", processTick);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.tank.readFromNBT(tag);
        this.processedDreamAmount = tag.getInt("ProcessedDreamAmount");
        this.processTick = tag.getInt("ProcessTick");
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
        return 1000;
    }

    @Override
    public void updateFluidTo(FluidStack fluid) {
        this.tank.setFluid(fluid);
    }

    public double getDreamRadius() {
        return getAmount() / 150.0;
    }

    public int getAmount() {
        return this.tank.getFluidAmount();
    }

    public int getProcessedDreamAmount() {
        return this.processedDreamAmount;
    }

    public void fillDream() {
        this.tank.fill(new FluidStack(AllFluids.dream.asFluid(), level.getRandom().nextInt(200, 600)), IFluidHandler.FluidAction.EXECUTE);
    }
}
