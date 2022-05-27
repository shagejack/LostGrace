package shagejack.lostgrace.registries.item;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.contents.item.blackKnife.BlackKnifeItem;

public class ItemPropertyOverridesRegistry {

    @SubscribeEvent
    public static void propertyOverrideRegistry(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(AllItems.blackKnife.get(), LostGrace.asResource("blood"), (itemStack, clientLevel, livingEntity, entityId) -> itemStack.getOrCreateTag().getInt("Blood") / 1000F);

            ItemProperties.register(AllItems.blackKnife.get(), LostGrace.asResource("invisible"), (itemStack, clientLevel, livingEntity, entityId) -> BlackKnifeItem.isInvisible(itemStack) ? 1.0F : 0.0F);
        });
    }


}
