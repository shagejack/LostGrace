package shagejack.lostgrace.contents.block.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import shagejack.lostgrace.foundation.utility.BlockReference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SpellShape {
    public static final SpellShape NULL = new SpellShape(new ArrayList<>(), false, $ -> false);

    List<BlockReference> blockReferences;
    boolean rotatable;
    Predicate<Level> levelPredicate;

    public SpellShape(List<BlockReference> blockReferences, boolean rotatable, Predicate<Level> levelPredicate) {
        this.blockReferences = blockReferences;
        this.rotatable = rotatable;
        this.levelPredicate = levelPredicate;
    }

    public boolean check(Level level, BlockPos pos) {
        if (isEmpty())
            return false;

        if (!levelPredicate.test(level))
            return false;

        if (!rotatable) {
            return blockReferences.stream().allMatch(ref -> ref.check(level, pos));
        } else {
            return blockReferences.stream().allMatch(ref -> ref.check(level, pos, Direction.EAST)) ||
                    blockReferences.stream().allMatch(ref -> ref.check(level, pos, Direction.SOUTH)) ||
                    blockReferences.stream().allMatch(ref -> ref.check(level, pos, Direction.WEST)) ||
                    blockReferences.stream().allMatch(ref -> ref.check(level, pos, Direction.NORTH));
        }
    }

    public boolean isEmpty() {
        return this.blockReferences.isEmpty();
    }

    public static class Builder {
        List<BlockReference> blockReferences = new ArrayList<>();
        boolean rotatable = false;
        Predicate<Level> levelPredicate = $ -> true;
        Predicate<BlockState> currentStatePredicate = BlockBehaviour.BlockStateBase::isAir;

        public Builder append(BlockReference... blockRef) {
            this.blockReferences.addAll(List.of(blockRef));
            return this;
        }

        public Builder append(int x, int y, int z) {
            this.blockReferences.add(BlockReference.of(x, y, z, this.currentStatePredicate));
            return this;
        }

        public Builder append(int x, int z) {
            this.blockReferences.add(BlockReference.of(x, 0, z, this.currentStatePredicate));
            return this;
        }

        public Builder statePredicate(Predicate<BlockState> statePredicate) {
            this.currentStatePredicate = statePredicate;
            return this;
        }

        public Builder block(Block block) {
            return this.statePredicate(state -> state.is(block));
        }

        public Builder instance(Block block) {
            return this.instance(block.getClass());
        }

        public Builder instance(Class<? extends Block> blockClazz) {
            return this.statePredicate(state -> blockClazz.isInstance(state.getBlock()));
        }

        public Builder state(Block block, Predicate<BlockState> extraStatePredicate) {
            return this.statePredicate(state -> state.is(block) && extraStatePredicate.test(state));
        }

        public Builder attachExtraStatePredicate(Predicate<BlockState> extraStatePredicate) {
            return this.statePredicate(this.currentStatePredicate.and(extraStatePredicate));
        }

        public Builder air() {
            return this.statePredicate(BlockBehaviour.BlockStateBase::isAir);
        }

        public Builder spell(RuneType runeType) {
            return this.block(runeType.getRuneBlock());
        }

        public Builder tag(TagKey<Block> tag) {
            return this.statePredicate(state -> state.is(tag));
        }

        public Builder rotatable() {
            this.rotatable = true;
            return this;
        }

        public Builder levelPredicate(Predicate<Level> levelPredicate) {
            this.levelPredicate = levelPredicate;
            return this;
        }

        public Builder fullSquareWithAir(int range) {
            return this.fullSquareWithAir(0, range);
        }

        public Builder fullSquareWithAir(int yOffset, int range) {
            for (int i = -range; i <= range; i++) {
                for (int j = -range; j <= range; j++) {
                    BlockPos offset = new BlockPos(i, yOffset, j);
                    if (blockReferences.stream().noneMatch(ref -> ref.offset().equals(offset)))
                        this.blockReferences.add(BlockReference.of(offset, Blocks.AIR));
                }
            }
            return this;
        }

        public SpellShape build() {
            return new SpellShape(this.blockReferences, this.rotatable, this.levelPredicate);
        }

    }
}
