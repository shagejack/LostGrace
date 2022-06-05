package shagejack.lostgrace.contents.block.spell.unkown;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import shagejack.lostgrace.contents.block.spell.Spell;
import shagejack.lostgrace.contents.block.spell.SpellType;

public enum ChalkSpellType {
    NULL(Spell.NULL),
    SUMMON_BLACK_KNIFE_ASSASSIN(
            new Spell.Builder()
                    .spell(SpellType.UNKNOWN)
                    .append(0, 0)
                    .append(-2, -2)
                    .append(2, -2)
                    .append(-2, 2)
                    .append(2, 2)
                    .append(3, -1)
                    .append(3, 0)
                    .append(3, 1)
                    .append(-3, -1)
                    .append(-3, 0)
                    .append(-3, 1)
                    .append(-1, 3)
                    .append(0, 3)
                    .append(1, 3)
                    .append(-1, -3)
                    .append(0, -3)
                    .append(1, -3)

                    .block(Blocks.CRIMSON_ROOTS)
                    .append(2, 0)
                    .append(-2, 0)
                    .append(0, 2)
                    .append(0, -2)

                    .block(Blocks.WARPED_ROOTS)
                    .append(2, 1)
                    .append(2, -1)
                    .append(-2, 1)
                    .append(-2, -1)
                    .append(1, 2)
                    .append(-1, 2)
                    .append(1, -2)
                    .append(-1, -2)

                    .state(Blocks.WHITE_CANDLE, state -> state.getValue(BlockStateProperties.LIT))
                    .append(1, 1)
                    .append(1, -1)
                    .append(-1 , 1)
                    .append(-1, -1)

                    .block(Blocks.AMETHYST_BLOCK)
                    .append(0, -1, 0)

                    .block(Blocks.CRIMSON_NYLIUM)
                    .append(1, -1, 0)
                    .append(2, -1, 0)
                    .append(-1, -1, 0)
                    .append(-2, -1, 0)
                    .append(0, -1, 1)
                    .append(0, -1, 2)
                    .append(0, -1, -1)
                    .append(0, -1, -2)

                    .block(Blocks.WARPED_NYLIUM)
                    .append(1, -1, 1)
                    .append(-1, -1, 1)
                    .append(1, -1, -1)
                    .append(-1, -1, -1)
                    .append(2, -1, -1)
                    .append(2, -1, 1)
                    .append(-2, -1, -1)
                    .append(-2, -1, 1)
                    .append(-1, -1, 2)
                    .append(1, -1, 2)
                    .append(-1, -1, -2)
                    .append(1, -1, -2)
                    
                    .block(Blocks.PURPUR_BLOCK)
                    .append(-2, -1, -2)
                    .append(2, -1, -2)
                    .append(-2, -1,2)
                    .append(2, -1,2)
                    .append(3, -1,-1)
                    .append(3, -1,0)
                    .append(3, -1,1)
                    .append(-3, -1,-1)
                    .append(-3, -1,0)
                    .append(-3, -1,1)
                    .append(-1, -1, 3)
                    .append(0, -1,3)
                    .append(1, -1,3)
                    .append(-1, -1,-3)
                    .append(0, -1,-3)
                    .append(1, -1,-3)

                    .fullSquareWithAir(3)
                    .levelPredicate(Level::isNight)
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
