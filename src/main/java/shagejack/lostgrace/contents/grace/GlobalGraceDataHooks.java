package shagejack.lostgrace.contents.grace;

import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.server.ServerLifecycleHooks;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.foundation.network.AllPackets;
import shagejack.lostgrace.foundation.network.packet.GlobalGraceSetLoadingPacket;

import java.io.File;
import java.util.Set;

public class GlobalGraceDataHooks {

    public static void loadData() {
        File graceFile = new File(ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.ROOT).toFile(), "graces.dat");

        try {
            CompoundTag nbt = NbtIo.read(graceFile);
            GlobalGraceSet.graceSet = deserializeNBT(nbt);
            syncToClient(GlobalGraceSet.graceSet);
        } catch (Throwable throwable) {
            LostGrace.LOGGER.throwing(throwable);
        }
    }

    public static void saveData() {
        File graceFile = new File(ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.ROOT).toFile(), "graces.dat");

        try {
            CompoundTag tag = GlobalGraceDataHooks.serializeNBT(GlobalGraceSet.getGraceSet());
            NbtIo.write(tag, graceFile);
        } catch (Throwable throwable) {
            LostGrace.LOGGER.throwing(throwable);
        }
    }

    public static CompoundTag serializeNBT(Set<Grace> graces) {
        CompoundTag tag = new CompoundTag();
        CompoundTag data = new CompoundTag();
        int i = 0;
        for (Grace grace : graces) {
            tag.put("Grace" + i, grace.serializeNBT());
            i++;
        }

        data.put("GlobalGraceSet", tag);
        return data;
    }

    public static Set<Grace> deserializeNBT(CompoundTag data) {
        Set<Grace> graces = Sets.newConcurrentHashSet();
        if (data != null) {
            CompoundTag tag = data.getCompound("GlobalGraceSet");
            int i = 0;
            while (tag.contains("Grace" + i, Tag.TAG_COMPOUND)) {
                graces.add(new Grace(tag.getCompound("Grace" + i)));
                i++;
            }
        }
        return graces;
    }

    // public static void clearData() {}

    public static void syncToClient(Set<Grace> graceSet) {
        AllPackets.sendToAll(new GlobalGraceSetLoadingPacket(graceSet));
    }

}
