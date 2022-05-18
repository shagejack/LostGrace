package shagejack.lostgrace.contents.grace;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import shagejack.lostgrace.foundation.network.AllPackets;
import shagejack.lostgrace.foundation.network.packet.PlayerGraceDataPacket;
import shagejack.lostgrace.foundation.utility.Constants;
import shagejack.lostgrace.foundation.utility.Vector3;

import java.util.List;
import java.util.Set;

public interface IGraceHandler extends INBTSerializable<CompoundTag> {

    default Grace getLastGrace() {
        if (getLastGraceIndex() < getAllGracesFound().size())
            return getAllGracesFound().get(getLastGraceIndex());

        return Grace.NULL;
    }

    default boolean contains(Grace grace) {
        for (Grace found : getAllGracesFound()) {
            if (found.equals(grace))
                return true;
        }
        return false;
    }

    int getLastGraceIndex();

    List<Grace> getAllGracesFound();

    /**
     * The method for player to visit grace.
     * @param grace The grace visited.
     * @return if it's the first time to visit this grace
     */
    boolean visitGrace(Grace grace);

    void copyFrom(IGraceHandler graceHandler);

    void checkGrace(Set<Grace> graceSet);

    void activateGrace();

    void deactivateGrace();

    boolean isGraceActivated();

    Vector3 getGraceActivatedPos();

    void setGraceActivatedPos(Vector3 pos);

    default void setGraceActivatedPos(BlockPos pos) {
        setGraceActivatedPos(Vector3.of(pos));
    }

    default void syncToClient(ServerPlayer player) {
        AllPackets.sendToPlayer(player, new PlayerGraceDataPacket(this));
    }

    default void tryDeactivateGrace(ServerPlayer player) {
        if (Vector3.of(player).distance(getGraceActivatedPos().add(0.5, Constants.GRACE_DISTANCE_Y_OFFSET, 0.5)) > Constants.GRACE_MAX_DISTANCE) {
            deactivateGrace();
            syncToClient(player);
        }
    }
}
