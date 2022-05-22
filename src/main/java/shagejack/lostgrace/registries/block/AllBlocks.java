package shagejack.lostgrace.registries.block;

import shagejack.lostgrace.contents.block.bloodAltar.BloodAltarBlock;
import shagejack.lostgrace.contents.block.grace.GraceBlock;
import shagejack.lostgrace.registries.item.ItemBuilder;
import shagejack.lostgrace.registries.record.ItemBlock;

public class AllBlocks {
    public static final ItemBlock grace = new BlockBuilder()
            .name("grace")
            .properties(properties -> properties.lightLevel(state -> 15).noCollission())
            .buildBlock(GraceBlock::new)
            .buildItem(ItemBuilder::noTab);

    public static final ItemBlock bloodAltar = new BlockBuilder()
            .name("blood_altar")
            .properties(properties -> properties)
            .buildBlock(BloodAltarBlock::new)
            .buildItem();
}
