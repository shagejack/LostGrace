package shagejack.lostgrace.foundation.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import shagejack.lostgrace.LostGrace;

import java.util.function.Predicate;

public record BlockReference(BlockPos offset, Predicate<BlockState> statePredicate) {
    public static BlockReference of(int x, int y, int z, Predicate<BlockState> statePredicate) {
        return new BlockReference(new BlockPos(x, y, z), statePredicate);
    }

    public static BlockReference of(BlockPos offset, Block block) {
        return new BlockReference(offset, state -> state.is(block));
    }

    public static BlockReference of(int x, int y, int z, Block block) {
        if (block == Blocks.AIR) {
            return new BlockReference(new BlockPos(x, y, z), BlockBehaviour.BlockStateBase::isAir);
        }

        return new BlockReference(new BlockPos(x, y, z), state -> state.is(block));
    }

    public static BlockReference of(BlockPos offset, TagKey<Block> tag) {
        return new BlockReference(offset, state -> state.is(tag));
    }

    public static BlockReference of(int x, int y, int z, TagKey<Block> tag) {
        return new BlockReference(new BlockPos(x, y, z), state -> state.is(tag));
    }

    public boolean check(Level level, BlockPos pos) {
        return check(level, pos, Direction.EAST);
    }

    public boolean check(Level level, BlockPos pos, Direction direction) {
        return statePredicate.test(level.getBlockState(pos.offset(getRotated(offset, direction))));
    }

    /**
     * Rotating BlockPos
     * around (0, 0, 0)
     * Only four directions(East(X+), South(Z+), West(X-), North(Z-)) available
     */
    private BlockPos getRotated(BlockPos pos, Direction direction) {
        return switch(direction) {
            case EAST -> pos;
            case SOUTH -> new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
            case WEST -> new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
            case NORTH -> new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
            default -> throw new IllegalArgumentException();
        };
    }
}
