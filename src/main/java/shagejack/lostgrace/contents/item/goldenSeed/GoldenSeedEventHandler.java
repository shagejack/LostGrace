package shagejack.lostgrace.contents.item.goldenSeed;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import shagejack.lostgrace.registries.item.AllItems;

public class GoldenSeedEventHandler {
    @SubscribeEvent
    public static void onGoldenSeedSpawn(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            if (itemEntity.getItem().is(AllItems.goldenSeed.get())) {
                event.getEntity().setInvulnerable(true);
            }
        }
    }
}
