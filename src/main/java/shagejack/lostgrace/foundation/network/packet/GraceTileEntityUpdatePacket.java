package shagejack.lostgrace.foundation.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import shagejack.lostgrace.contents.block.grace.GraceTileEntity;

import java.util.function.Supplier;

public class GraceTileEntityUpdatePacket extends SimplePacketBase {

    BlockPos pos;
    String name;
    boolean isOnUse;
    boolean isLocked;

    public GraceTileEntityUpdatePacket(BlockPos pos, String name, boolean isOnUse, boolean isLocked) {
        this.pos = pos;
        this.name = name;
        this.isOnUse = isOnUse;
        this.isLocked = isLocked;
    }

    public GraceTileEntityUpdatePacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.name = buffer.readUtf();
        this.isOnUse = buffer.readBoolean();
        this.isLocked = buffer.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeUtf(name);
        buffer.writeBoolean(isOnUse);
        buffer.writeBoolean(isLocked);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            ClientLevel world = Minecraft.getInstance().level;

            if (world == null)
                return;

            BlockEntity tile = world.getBlockEntity(pos);

            if (tile instanceof GraceTileEntity graceTile) {
                graceTile.setGraceName(name);
                graceTile.setOnUse(isOnUse);
                graceTile.setLocked(isLocked);
            }
        });

        ctx.setPacketHandled(true);
    }
}
