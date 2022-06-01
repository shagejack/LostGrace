package shagejack.lostgrace.contents.block.spell.unkown;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import shagejack.lostgrace.contents.block.spell.Spell;
import shagejack.lostgrace.contents.block.spell.SpellType;

public enum ChalkSpellType {
    NULL(Spell.NULL),
    SUMMON_BLACK_KNIFE_ASSASSIN(
            new Spell.Builder()
                    .append(0, 0, SpellType.UNKNOWN)
                    .append(-2, -2, SpellType.UNKNOWN)
                    .append(2, -2, SpellType.UNKNOWN)
                    .append(-2, 2, SpellType.UNKNOWN)
                    .append(2, 2, SpellType.UNKNOWN)
                    .append(3, -1, SpellType.UNKNOWN)
                    .append(3, 0, SpellType.UNKNOWN)
                    .append(3, 1, SpellType.UNKNOWN)
                    .append(-3, -1, SpellType.UNKNOWN)
                    .append(-3, 0, SpellType.UNKNOWN)
                    .append(-3, 1, SpellType.UNKNOWN)
                    .append(-1, 3, SpellType.UNKNOWN)
                    .append(0, 3, SpellType.UNKNOWN)
                    .append(1, 3, SpellType.UNKNOWN)
                    .append(-1, -3, SpellType.UNKNOWN)
                    .append(0, -3, SpellType.UNKNOWN)
                    .append(1, -3, SpellType.UNKNOWN)
                    .fullSquareWithAir(3)
                    .build()
    )
    ;

    private final Spell spell;

    ChalkSpellType(Spell spell) {
        this.spell = spell;
    }

    public Spell getSpell() {
        return this.spell;
    }

    public boolean check(Level level, BlockPos pos) {
        if (spell.isEmpty())
            return false;
        return this.spell.check(level, pos);
    }
}
