package shagejack.lostgrace.contents.grace;

import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.contents.block.grace.GraceTileEntity;

import java.io.*;
import java.util.List;
import java.util.Set;

public class GlobalGraceSet {

    private static int IDLE = 1200;

    public static Set<Grace> graceSet = Sets.newConcurrentHashSet();

    public static Set<Grace> getGraceSet() {
        return graceSet;
    }

    public static boolean contains(Grace grace) {
        return graceSet.contains(grace);
    }

    public static void addGrace(Grace grace) {
        if (grace != null && grace.getLevel() != null &&  !grace.equals(Grace.NULL) && !grace.getLevel().isClientSide()) {
            if (graceSet.add(grace)) {
                checkGraceForPlayers();
            }
        }
    }

    public static void removeGrace(Grace grace) {
        if (grace != null && grace.getLevel() != null && !grace.getLevel().isClientSide()) {
            if (graceSet.remove(grace)) {
                checkGraceForPlayers();
            }
        }
    }

    @SubscribeEvent
    public static void tickGrace(TickEvent.ServerTickEvent event) {
        // check grace existence in case of unexpected circumstances
        if (IDLE > 0) {
            IDLE--;
        } else {
            boolean modified = graceSet.removeIf(grace -> {
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

            // check grace for players
            if (modified)
                checkGraceForPlayers();

            IDLE = 1200;
        }

        List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY).ifPresent(graceData -> {
                if (graceData.isGraceActivated()) {
                    graceData.tryDeactivateGrace(player);
                }
            });
        }
    }

    public static void checkGraceForPlayers() {
        List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY).ifPresent(graceData -> {
                graceData.checkGrace(getGraceSet());
                graceData.syncToClient(player);
            });
        }
    }

    @SubscribeEvent
    public static void onDataLoad(WorldEvent.Load event) {
        GlobalGraceDataHooks.loadData();
    }

    @SubscribeEvent
    public static void onDataSave(WorldEvent.Save event) {
        GlobalGraceDataHooks.saveData();
    }

}
