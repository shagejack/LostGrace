package shagejack.lostgrace.contents.item.record;

import net.minecraft.world.item.RecordItem;
import shagejack.lostgrace.registries.AllSoundEvents;

public class KappaRecordItem extends RecordItem {

    public KappaRecordItem(Properties builder) {
        super(15, () -> AllSoundEvents.KAPPA, builder);
    }
}
