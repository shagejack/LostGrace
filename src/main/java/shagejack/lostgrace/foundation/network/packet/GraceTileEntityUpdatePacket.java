package shagejack.lostgrace.foundation.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import shagejack.lostgrace.contents.block.grace.GraceTileEntity;
import shagejack.lostgrace.contents.grace.Grace;

import java.util.function.Supplier;
import java.util.logging.Level;

public class GraceTileEntityUpdatePacket extends SimplePacketBase {

    BlockPos pos;
    String name;

    public GraceTileEntityUpdatePacket(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public GraceTileEntityUpdatePacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.name = buffer.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeUtf(name);
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
            }
        });

        ctx.setPacketHandled(true);
    }
}
