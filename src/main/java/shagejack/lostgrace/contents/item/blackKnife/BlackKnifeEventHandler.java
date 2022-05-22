package shagejack.lostgrace.contents.item.blackKnife;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import shagejack.lostgrace.registries.item.AllItems;

public class BlackKnifeEventHandler {
    @SubscribeEvent
    public static void onBlackKnifeDropOnFloor(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            if (itemEntity.getItem().is(AllItems.blackKnife.get())) {
                event.getEntity().setInvulnerable(true);
            }
        }
    }
}
