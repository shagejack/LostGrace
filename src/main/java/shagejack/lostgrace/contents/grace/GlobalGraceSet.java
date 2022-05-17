package shagejack.lostgrace.contents.grace;

import com.google.common.collect.Sets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import shagejack.lostgrace.contents.block.grace.GraceTileEntity;

import java.util.List;
import java.util.Set;

public class GlobalGraceSet {

    public static Set<Grace> graceSet = Sets.newConcurrentHashSet();

    public static Set<Grace> getGraceSet() {
        return graceSet;
    }

    public static boolean contains(Grace grace) {
        return graceSet.contains(grace);
    }

    public static void addGrace(Grace grace) {
        if (grace != null && grace != Grace.NULL) {
            graceSet.add(grace);
        }
    }

    public static void removeGrace(Grace grace) {
        if (grace != null) {
            graceSet.remove(grace);
        }
    }

    @SubscribeEvent
    public static void tickGrace(TickEvent.ServerTickEvent event) {
        // check grace existence
        graceSet.removeIf(grace -> {
            if (grace != null && grace.getLevel() != null) {
                // only check grace in loaded chunk
                if (grace.getLevel().isLoaded(grace.getPos())) {
                    if (grace.getLevel().getBlockEntity(grace.getPos()) instanceof GraceTileEntity te) {
                        return !te.getGrace().equals(grace);
                    }
                } else {
                    return false;
                }
            }
            return true;
        });

        // check grace for player
        List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            LazyOptional<IGraceHandler> graceHandler = player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY);
            graceHandler.ifPresent(graceData -> {
                graceData.checkGrace(graceSet);
                graceData.syncToClient(player);
                if (graceData.isGraceActivated()) {
                    graceData.tryDeactivateGrace(player);
                }
            });
        }
    }

}
