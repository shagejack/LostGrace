package shagejack.lostgrace.foundation.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import shagejack.lostgrace.contents.grace.GraceProvider;
import shagejack.lostgrace.contents.grace.IGraceHandler;
import shagejack.lostgrace.contents.grace.PlayerGraceData;

import java.util.function.Supplier;

public class PlayerGraceDataPacket extends SimplePacketBase {

    IGraceHandler data;

    public PlayerGraceDataPacket(IGraceHandler data) {
        this.data = data;
    }

    public PlayerGraceDataPacket(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readNbt();
        PlayerGraceData graceData = new PlayerGraceData();
        if (tag != null) {
            graceData.deserializeNBT(tag);
        }
        this.data = graceData;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(data.serializeNBT());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().player == null)
                return;

            Minecraft.getInstance().player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY).ifPresent(graceHandler -> {
                graceHandler.copyFrom(data);
            });
        });

        ctx.setPacketHandled(true);
    }
}
