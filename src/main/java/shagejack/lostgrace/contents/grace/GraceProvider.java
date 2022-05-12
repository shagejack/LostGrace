package shagejack.lostgrace.contents.grace;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GraceProvider implements ICapabilitySerializable<CompoundTag> {

    public static Capability<IGraceHandler> GRACE_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    protected PlayerGraceData graceData = null;
    protected LazyOptional<IGraceHandler> graceCapability = LazyOptional.of(this::createPlayerGraceData);

    public @NotNull PlayerGraceData createPlayerGraceData() {
        if (graceData == null) {
            this.graceData = new PlayerGraceData();
        }
        return this.graceData;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == GRACE_HANDLER_CAPABILITY) {
            return this.graceCapability.cast();
        }

        return LazyOptional.empty();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Override
    public CompoundTag serializeNBT() {
        return createPlayerGraceData().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerGraceData().deserializeNBT(nbt);
    }
}
