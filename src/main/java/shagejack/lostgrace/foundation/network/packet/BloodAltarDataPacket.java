package shagejack.lostgrace.foundation.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import shagejack.lostgrace.contents.block.bloodAltar.BloodAltarTileEntity;

public class BloodAltarDataPacket extends TileEntityDataPacket<BloodAltarTileEntity> {

    public boolean corrupt;

    public BloodAltarDataPacket(FriendlyByteBuf buffer) {
        super(buffer);
        this.corrupt = buffer.readBoolean();
    }

    public BloodAltarDataPacket(BlockPos pos, boolean corrupt) {
        super(pos);
        this.corrupt = corrupt;
    }

    @Override
    protected void writeData(FriendlyByteBuf buffer) {
        buffer.writeBoolean(corrupt);
    }

    @Override
    protected void handlePacket(BloodAltarTileEntity tile) {
        tile.syncFromPacket(corrupt);
    }
}
