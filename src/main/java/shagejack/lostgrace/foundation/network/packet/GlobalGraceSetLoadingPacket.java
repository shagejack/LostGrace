package shagejack.lostgrace.foundation.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import shagejack.lostgrace.contents.grace.GlobalGraceDataHooks;
import shagejack.lostgrace.contents.grace.GlobalGraceSet;
import shagejack.lostgrace.contents.grace.Grace;

import java.util.Set;
import java.util.function.Supplier;

public class GlobalGraceSetLoadingPacket extends SimplePacketBase {

    Set<Grace> graceSet;

    public GlobalGraceSetLoadingPacket(Set<Grace> graceSet) {
        this.graceSet = graceSet;
    }

    public GlobalGraceSetLoadingPacket(FriendlyByteBuf buffer) {
        this.graceSet = GlobalGraceDataHooks.deserializeNBT(buffer.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(GlobalGraceDataHooks.serializeNBT(this.graceSet));
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            ClientLevel world = Minecraft.getInstance().level;

            if (world == null)
                return;

            GlobalGraceSet.graceSet = this.graceSet;
        });

        ctx.setPacketHandled(true);
    }
}
