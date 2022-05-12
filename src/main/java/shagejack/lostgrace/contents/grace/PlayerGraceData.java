package shagejack.lostgrace.contents.grace;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerGraceData implements IGraceHandler {

    protected List<Grace> graces;
    protected int lastVisitedGraceIndex;

    public PlayerGraceData() {
        this.graces = new ArrayList<>();
        this.lastVisitedGraceIndex = 0;
    }
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        CompoundTag gracesList = new CompoundTag();
        for (int i = 0; i < graces.size(); i++) {
            gracesList.put("Grace" + i, graces.get(i).serializeNBT());
        }
        tag.put("GracesList", gracesList);
        tag.putInt("LastVisitedGraceIndex", lastVisitedGraceIndex);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        CompoundTag gracesListTag = nbt.getCompound("GracesList");
        List<Grace> graces = new ArrayList<>();
        int i = 0;
        while (gracesListTag.contains("Grace" + i, Tag.TAG_COMPOUND)) {
            graces.add(new Grace(gracesListTag.getCompound("Grace" + i)));
            i++;
        }
        this.graces = graces;
        this.lastVisitedGraceIndex = nbt.getInt("LastVisitedGraceIndex");
    }

    @Override
    public int getLastGraceIndex() {
        return this.lastVisitedGraceIndex;
    }

    @Override
    public List<Grace> getAllGracesFound() {
        return this.graces;
    }

    @Override
    public boolean visitGrace(Grace grace) {
        int i = 0;
        for (; i < graces.size(); i++) {
            if (graces.get(i).equals(grace)) {
                this.lastVisitedGraceIndex = i;
                break;
            }
        }
        if (i == graces.size()) {
            graces.add(grace);
            this.lastVisitedGraceIndex = graces.size() - 1;
            return true;
        }
        return false;
    }

    @Override
    public void copyFrom(IGraceHandler graceHandler) {
        this.graces = graceHandler.getAllGracesFound();
        this.lastVisitedGraceIndex = graceHandler.getLastGraceIndex();
    }

    @Override
    public void checkGrace(Set<Grace> graceSet) {
        this.graces = this.graces.stream()
                .filter(graceSet::contains)
                .collect(Collectors.toList());
    }
}
