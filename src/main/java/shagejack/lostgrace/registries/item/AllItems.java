package shagejack.lostgrace.registries.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.contents.item.goldenSeed.GoldenSeed;
import shagejack.lostgrace.contents.item.memoryOfGrace.MemoryOfGraceItem;
import shagejack.lostgrace.contents.item.record.DiesIraeRecordItem;
import shagejack.lostgrace.contents.item.record.KappaRecordItem;
import shagejack.lostgrace.registries.AllTabs;

public class AllItems {
    public static final RegistryObject<Item> memoryOfGrace = new ItemBuilder()
            .properties(properties -> properties.stacksTo(1))
            .name("memory_of_grace")
            .build(MemoryOfGraceItem::new);

    public static final RegistryObject<Item> goldenSeed = new ItemBuilder()
            .properties(properties -> properties.stacksTo(1))
            .name("golden_seed")
            .build(GoldenSeed::new);

    public static final RegistryObject<Item> musicDiscDiesIrae = new ItemBuilder()
            .properties(properties -> properties.stacksTo(1).tab(AllTabs.tabMain).rarity(Rarity.EPIC))
            .name("music_disc_dies_irae")
            .build(DiesIraeRecordItem::new);

    public static final RegistryObject<Item> musicDiscKappa = new ItemBuilder()
            .properties(properties -> properties.stacksTo(1).tab(AllTabs.tabMain).rarity(Rarity.EPIC))
            .name("music_disc_kappa")
            .build(KappaRecordItem::new);
}
