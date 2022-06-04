package shagejack.lostgrace.registries.loot;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import shagejack.lostgrace.LostGrace;

public class LootModifiersEventHandler {

    public static void registerLootModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().registerAll(
                new AddItemModifier.Serializer().setRegistryName(LostGrace.MOD_ID, "add_item"),
                new AddLootTableModifier.Serializer().setRegistryName(LostGrace.MOD_ID, "add_loot_table")
        );
    }
}
