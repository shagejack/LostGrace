package shagejack.lostgrace.contents.block.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class SpellHelper {
    public static boolean isSpell(Level level, BlockPos pos, SpellType type) {
        if (type == SpellType.NULL)
            return true;

        return level.getBlockState(pos).is(type.getSpellBlock());
    }

}
