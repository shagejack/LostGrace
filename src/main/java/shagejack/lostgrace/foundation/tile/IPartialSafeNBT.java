package shagejack.lostgrace.foundation.tile;

import net.minecraft.nbt.CompoundTag;

public interface IPartialSafeNBT {
	void writeSafe(CompoundTag compound, boolean clientPacket);
}