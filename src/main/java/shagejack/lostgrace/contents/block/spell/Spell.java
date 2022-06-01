package shagejack.lostgrace.contents.block.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import shagejack.lostgrace.foundation.utility.BlockReference;

import java.util.ArrayList;
import java.util.List;

public class Spell {
    public static final Spell NULL = new Spell(new ArrayList<>());

    List<BlockReference> blockReferences;

    public Spell(List<BlockReference> blockReferences) {
        this.blockReferences = blockReferences;
    }

    public boolean check(Level level, BlockPos pos) {
        if (isEmpty())
            return false;

        return blockReferences.stream().allMatch(ref -> ref.check(level, pos));
    }

    public boolean isEmpty() {
        return this.blockReferences.isEmpty();
    }

    public static class Builder {
        List<BlockReference> blockReferences = new ArrayList<>();

        public Builder append(BlockReference... blockRef) {
            this.blockReferences.addAll(List.of(blockRef));
            return this;
        }

        public Builder append(int x, int z, Block block) {
            this.blockReferences.add(BlockReference.of(x, 0, z, block));
            return this;
        }

        public Builder append(int x, int z, SpellType spellType) {
            this.blockReferences.add(BlockReference.of(x, 0, z, spellType.getSpellBlock()));
            return this;
        }

        public Builder fullSquareWithAir(int range) {
            for (int i = -range; i <= range; i++) {
                for (int j = -range; j <= range; j++) {
                    BlockPos offset = new BlockPos(i, 0, j);
                    if (blockReferences.stream().noneMatch(ref -> ref.offset().equals(offset)))
                        this.blockReferences.add(new BlockReference(offset, Blocks.AIR));
                }
            }
            return this;
        }

        public Spell build() {
            return new Spell(this.blockReferences);
        }

    }
}
