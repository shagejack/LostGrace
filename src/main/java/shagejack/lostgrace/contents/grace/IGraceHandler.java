package shagejack.lostgrace.contents.grace;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

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
                return false;
        }
        return true;
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
}
