package shagejack.lostgrace.contents.block.bloodAltar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
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
import shagejack.lostgrace.foundation.network.AllPackets;
import shagejack.lostgrace.foundation.network.packet.BloodAltarDataPacket;
import shagejack.lostgrace.foundation.network.packet.FluidUpdatePacket;
import shagejack.lostgrace.foundation.tile.BaseTileEntity;
import shagejack.lostgrace.foundation.utility.Constants;
import shagejack.lostgrace.foundation.utility.DropUtils;
import shagejack.lostgrace.foundation.utility.LevelUtils;
import shagejack.lostgrace.foundation.utility.Vector3;
import shagejack.lostgrace.registries.AllSoundEvents;
import shagejack.lostgrace.registries.block.AllBlocks;
import shagejack.lostgrace.registries.fluid.AllFluids;
import shagejack.lostgrace.registries.item.AllItems;
import shagejack.lostgrace.registries.tile.AllTileEntities;

import java.util.*;
import java.util.List;

public class BloodAltarTileEntity extends BaseTileEntity implements ITankTileEntity, FluidUpdatePacket.IFluidPacketReceiver {

    public static final int CAPACITY = 1000;
    public static final int TOTAL_TICKS = 3600;
    public static final int PHASE_ONE_END = TOTAL_TICKS / 3;
    public static final int PHASE_TWO_END = 2 * TOTAL_TICKS / 3;

    public static final double IMPACT_RADIUS_MIN = Constants.IMPACT_MAX_RADIUS * 0.333;
    public static final double IMPACT_RADIUS_MED = Constants.IMPACT_MAX_RADIUS * 0.666;
    public static final double IMPACT_RADIUS_MAX = Constants.IMPACT_MAX_RADIUS;
    public static final int IMPACT_DELAY = 60;

    public FluidTankBase<BloodAltarTileEntity> bloodTank;

    LazyOptional<IFluidHandler> tankHandlerLazyOptional;

    public boolean corrupt;
    public boolean isStarted;
    public int remainingTicks;

    public final long seed;
    private final Random random;

    public BloodAltarTileEntity(BlockPos pos, BlockState state) {
        super(AllTileEntities.bloodAltar.get(), pos, state);
        this.bloodTank = new FluidTankBase<>(this, CAPACITY, fluidStack -> fluidStack.getFluid().isSame(AllFluids.profaneBlood.asFluid()) || fluidStack.getFluid().isSame(AllFluids.sacredBlood.asFluid()));
        this.tankHandlerLazyOptional = LazyOptional.of(() -> bloodTank);
        this.seed = pos.asLong();
        this.random = new Random(seed);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null)
            return;

        if (isStarted && remainingTicks > 0) {
            remainingTicks--;

            switch(getPhase()) {
                case GROW -> {
                    expandFresh();
                }
                case BREED, IMPACT_PRELUDE -> {
                    if (!level.isClientSide()) {
                        level.getEntitiesOfClass(LivingEntity.class, new AABB(getBlockPos()).inflate(getBreedSphereRadius())).stream().filter(entity -> Vector3.of(entity).distance(Vector3.atCenterOf(getBlockPos())) <= getBreedSphereRadius()).forEach(LivingEntity::kill);
                    }
                }
                case DECAY -> {
                    rotFresh();
                }
                case IMPACT_EMERGENCE -> {
                    if (remainingTicks == TOTAL_TICKS - PHASE_TWO_END) {
                        if (level.isClientSide()) {
                            level.playLocalSound(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), AllSoundEvents.DIES_IRAE, SoundSource.RECORDS, 1.0f, 1.0f, false);
                        }
                    }

                    int impactTick = (TOTAL_TICKS - getRemainingTicks()) - PHASE_TWO_END - IMPACT_DELAY;

                    if (impactTick > 0) {
                        if (impactTick < 60) {
                            if (impactTick % 4 == 0)
                                LevelUtils.replaceInSphere(level, getBlockPos(), impactTick / 60.0 * IMPACT_RADIUS_MIN, AllBlocks.fresh.block().get().defaultBlockState(), ($, state) -> this.random.nextDouble() < 0.5 && !state.is(AllBlocks.bloodAltar.block().get()));
                        } else if (impactTick < 120) {
                            if (impactTick % 4 == 0)
                                LevelUtils.replaceInSphere(level, getBlockPos(), (impactTick - 60) / 60.0 * IMPACT_RADIUS_MED, AllBlocks.fresh.block().get().defaultBlockState(), ($, state) -> this.random.nextDouble() < 0.8 && !state.is(AllBlocks.bloodAltar.block().get()));
                        } else if (impactTick < 240) {
                            if (impactTick % 4 == 0)
                                LevelUtils.replaceInSphere(level, getBlockPos(), (impactTick - 120) / 120.0 * IMPACT_RADIUS_MAX, AllBlocks.fresh.block().get().defaultBlockState(), ($, state) -> !state.is(AllBlocks.bloodAltar.block().get()));
                        }
                    }

                }
                case NONE -> {}
            }

        } else {
            if (isStarted) {
                if (corrupt) {
                    endCorrupt();
                } else {
                    endNormal();
                }
            }

            isStarted = false;
            corrupt = false;
        }
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        bloodTank.writeToNBT(tag);
        tag.putInt("RemainingTicks", remainingTicks);
        tag.putBoolean("Corrupt", corrupt);
        tag.putBoolean("IsStarted", isStarted);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        bloodTank.readFromNBT(tag);
        remainingTicks = tag.getInt("RemainingTicks");
        corrupt = tag.getBoolean("Corrupt");
        isStarted = tag.getBoolean("IsStarted");
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
        return CAPACITY;
    }

    @Override
    public void updateFluidTo(FluidStack fluid) {
        this.bloodTank.setFluid(fluid);
    }

    public boolean isEmpty() {
        return this.bloodTank.isEmpty();
    }

    public boolean tryStart() {
        if (level == null || remainingTicks > 0)
            return false;

        if (bloodTank.isFull()) {
            start();
        }

        return false;
    }

    public void start() {
        if (level == null)
            return;

        this.isStarted = true;

        this.remainingTicks = TOTAL_TICKS;
        if (bloodTank.getFluid().getFluid().isSame(AllFluids.profaneBlood.asFluid()) && !level.isClientSide() && level.getRandom().nextDouble() < Constants.IMPACT_EVENT_CHANCE) {
            this.corrupt = true;
        }

        syncToClient();

        this.bloodTank.empty();

        level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BloodAltarBlock.LOCKED, true));
    }

    public void endNormal() {
        if (level == null)
            return;

        level.setBlockAndUpdate(getBlockPos().above(), Blocks.AIR.defaultBlockState());
        DropUtils.dropItemStack(level, getBlockPos().above(), new ItemStack(AllItems.goldenSeed.get()));

        level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BloodAltarBlock.LOCKED, false));
    }

    public void endCorrupt() {
        if (level == null)
            return;

        level.setBlockAndUpdate(getBlockPos(), AllBlocks.fresh.block().get().defaultBlockState());
    }

    public int fillTankBlood(FluidStack stack, IFluidHandler.FluidAction action) {
        if (stack.isEmpty())
            return 0;

        if (stack.getFluid().isSame(AllFluids.sacredBlood.asFluid())) {
            if (bloodTank.isEmpty() || bloodTank.getFluid().getFluid().isSame(AllFluids.sacredBlood.asFluid())) {
                return bloodTank.fill(stack, action);
            } else {
                return bloodTank.fill(new FluidStack(AllFluids.profaneBlood.asFluid(), stack.getAmount()), action);
            }
        } else if (stack.getFluid().isSame(AllFluids.profaneBlood.asFluid())) {
            return bloodTank.fill(new FluidStack(AllFluids.profaneBlood.asFluid(), stack.getAmount()), action);
        }

        return 0;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isCorrupt() {
        return corrupt;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    public void expandFresh() {
        if (level == null)
            return;

        int tries = 0;
        BlockPos pos = getBlockPos();

        while (tries < 20) {

            List<Direction> directions = Arrays.asList(Direction.values());
            Collections.shuffle(directions, this.random);

            boolean placed = false;

            for (Direction direction : directions) {
                BlockPos posTemp = pos.relative(direction);
                if (canReplace(level.getBlockState(posTemp))) {
                    level.setBlockAndUpdate(posTemp, AllBlocks.fresh.block().get().defaultBlockState());
                    pos = posTemp;
                    placed = true;
                    break;
                }
            }

            if (placed)
                break;

            pos = pos.relative(Direction.getRandom(this.random));

            tries++;
        }
    }

    public void rotFresh() {
        if (level == null)
            return;

        int tries = 0;
        BlockPos pos = getBlockPos();

        while (tries < 25) {

            List<Direction> directions = Arrays.asList(Direction.values());
            Collections.shuffle(directions, this.random);

            boolean placed = false;

            for (Direction direction : directions) {
                BlockPos posTemp = pos.relative(direction);
                if (level.getBlockState(posTemp).is(AllBlocks.fresh.block().get())) {
                    level.setBlockAndUpdate(posTemp, AllBlocks.rottenFresh.block().get().defaultBlockState());
                    pos = posTemp;
                    placed = true;
                    break;
                }
            }

            if (placed)
                break;

            pos = pos.relative(Direction.getRandom(this.random));

            tries++;
        }
    }

    private boolean canReplace(BlockState state) {
        return !(state.is(AllBlocks.bloodAltar.block().get()) || state.is(AllBlocks.fresh.block().get()));
    }

    public BloodAltarPhase getPhase() {
        int ticks = TOTAL_TICKS - remainingTicks;
        if (ticks < PHASE_ONE_END) {
            return BloodAltarPhase.GROW;
        } else if (ticks < PHASE_TWO_END) {
            if (corrupt) {
                return BloodAltarPhase.IMPACT_PRELUDE;
            } else {
                return BloodAltarPhase.BREED;
            }
        } else if (ticks < TOTAL_TICKS) {
            if (corrupt) {
                return BloodAltarPhase.IMPACT_EMERGENCE;
            } else {
                return BloodAltarPhase.DECAY;
            }
        }
        return BloodAltarPhase.NONE;
    }

    public Random getRandom() {
        return this.random;
    }

    public double getBreedSphereRadius() {
        int minRadius = 8 + (int) Math.abs(getBlockPos().asLong() % 4);
        int extraRadius = 4 + (int) Math.abs(getBlockPos().asLong() % 9);
        double s = (getRemainingTicks() % 200) / 200.0;
        if (s > 0.5) {
            s = 1 - s;
        }

        s *= 2;

        return minRadius + s * extraRadius;
    }

    public void syncFromPacket(boolean corrupt) {
        this.corrupt = corrupt;
    }

    public void syncToClient() {
        if (level == null || level.isClientSide())
            return;

        AllPackets.sendToSameDimension(level, new BloodAltarDataPacket(getBlockPos(), corrupt));
    }
}
