package shagejack.lostgrace.foundation.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.foundation.network.packet.*;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public enum AllPackets {
    // Client to Server
    TELEPORT_GRACE(TeleportGracePacket.class, TeleportGracePacket::new, NetworkDirection.PLAY_TO_SERVER),

    // Server to Client
    DISCOVER_GRACE(DiscoverGracePacket.class, DiscoverGracePacket::new, NetworkDirection.PLAY_TO_CLIENT),
    PLAYER_GRACE_DATA(PlayerGraceDataPacket.class, PlayerGraceDataPacket::new, NetworkDirection.PLAY_TO_CLIENT),
    GRACE_TILE_UPDATE(GraceTileEntityUpdatePacket.class, GraceTileEntityUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT)

    ;

    public static final ResourceLocation CHANNEL_NAME = LostGrace.asResource("main");
    public static final int PROTOCOL_VERSION = 1;
    public static final String PROTOCOL_VERSION_STR = String.valueOf(PROTOCOL_VERSION);
    private static SimpleChannel CHANNEL;

    private LoadedPacket<?> packet;

    <T extends SimplePacketBase> AllPackets(Class<T> type, Function<FriendlyByteBuf, T> factory,
                                            NetworkDirection direction) {
        packet = new LoadedPacket<>(type, factory, direction);
    }

    public static void registerPackets() {
        CHANNEL = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .serverAcceptedVersions(PROTOCOL_VERSION_STR::equals)
                .clientAcceptedVersions(PROTOCOL_VERSION_STR::equals)
                .networkProtocolVersion(() -> PROTOCOL_VERSION_STR)
                .simpleChannel();

        for (AllPackets packet : values())
            packet.packet.register();
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }

    public static void sendToPlayer(ServerPlayer player, Object message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToSameDimension(Level world, Object message) {
        CHANNEL.send(PacketDistributor.DIMENSION.with(world::dimension), message);
    }

    public static void sendToAll(Object message) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), message);
    }

    public static void sendToNear(Level world, BlockPos pos, Object message) {
        CHANNEL.send(PacketDistributor.NEAR
                .with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), 256, world.dimension())), message);
    }

    public static void sendToNear(Level world, BlockPos pos, int range, Object message) {
        CHANNEL.send(PacketDistributor.NEAR
                .with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), range, world.dimension())), message);
    }

    private static class LoadedPacket<T extends SimplePacketBase> {
        private static int index = 0;

        private BiConsumer<T, FriendlyByteBuf> encoder;
        private Function<FriendlyByteBuf, T> decoder;
        private BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
        private Class<T> type;
        private NetworkDirection direction;

        private LoadedPacket(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
            encoder = T::write;
            decoder = factory;
            handler = T::handle;
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            CHANNEL.messageBuilder(type, index++, direction)
                    .encoder(encoder)
                    .decoder(decoder)
                    .consumer(handler)
                    .add();
        }
    }

}
