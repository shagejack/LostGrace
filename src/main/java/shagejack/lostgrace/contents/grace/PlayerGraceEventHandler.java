package shagejack.lostgrace.contents.grace;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import shagejack.lostgrace.LostGrace;

public class PlayerGraceEventHandler {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        LazyOptional<IGraceHandler> handler = player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY);
        handler.ifPresent(graceData -> {
            Grace grace = graceData.getLastGrace();
            if (grace != Grace.NULL && GlobalGraceSet.contains(grace)) {
                Level graceLevel = grace.getLevel();
                Vec3 pos = Vec3.atCenterOf(grace.getPos());
                if (graceLevel instanceof ServerLevel targetLevel) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.teleportTo(targetLevel, pos.x(), pos.y(), pos.z(), Mth.wrapDegrees(serverPlayer.getYRot()), Mth.wrapDegrees(serverPlayer.getXRot()));
                        graceData.visitGrace(grace, false);
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) return;

        if (!event.getObject().getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY).isPresent()) {
            event.addCapability(LostGrace.asResource("playergrace"), new GraceProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY).ifPresent(oldGrace -> {
                event.getPlayer().getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY).ifPresent(newGrace -> {
                    newGrace.copyFrom(oldGrace);
                    if (event.getPlayer().level instanceof ServerLevel) {
                        newGrace.checkGrace(GlobalGraceSet.getGraceSet());
                        newGrace.syncToClient((ServerPlayer) event.getPlayer());
                    }
                });
            });
            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(IGraceHandler.class);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer().level instanceof ServerLevel)
            checkGraceForPlayers((ServerPlayer) event.getPlayer());
    }

    public static void checkGraceForPlayers(ServerPlayer player) {
        player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY).ifPresent(graceData -> {
            graceData.checkGrace(GlobalGraceSet.getGraceSet());
            graceData.syncToClient(player);
        });
    }
}
