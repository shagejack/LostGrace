package shagejack.lostgrace.contents.item.record;

import net.minecraft.world.item.RecordItem;
import shagejack.lostgrace.registries.AllSoundEvents;

public class DiesIraeRecordItem extends RecordItem {

    public DiesIraeRecordItem(Properties builder) {
        super(15, () -> AllSoundEvents.DIES_IRAE, builder);
    }

}
