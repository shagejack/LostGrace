package shagejack.lostgrace.contents.block.spell;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import shagejack.lostgrace.registries.block.AllBlocks;
import shagejack.lostgrace.registries.record.ItemBlock;

public enum SpellType {
    UNKNOWN(AllBlocks.chalkSpell),
    NULL(null)

    ;

    ItemBlock spell;

    SpellType(ItemBlock spell) {
        this.spell = spell;
    }

    public Block getSpellBlock() {
        if (this.spell != null) {
            return this.spell.block().get();
        } else {
            return Blocks.AIR;
        }
    }
}
