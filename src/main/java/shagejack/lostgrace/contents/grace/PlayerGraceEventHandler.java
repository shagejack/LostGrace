package shagejack.lostgrace.contents.grace;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
                Level targetLevel = grace.getLevel();
                Vec3 pos = Vec3.atCenterOf(grace.getPos());
                if (targetLevel != null) {
                    if (!targetLevel.dimension().location().equals(player.getLevel().dimension().location())) {
                        player.changeDimension((ServerLevel) targetLevel);
                    }
                    player.teleportTo(pos.x(), pos.y(), pos.z());
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
                    if (event.getPlayer().level instanceof ServerLevel)
                        newGrace.syncToClient((ServerPlayer) event.getPlayer());
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
}
