package shagejack.lostgrace.foundation.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class LevelUtils {

    private LevelUtils() {
        throw new IllegalStateException(this.getClass().toString() + "should not be instantiated as it's a utility class.");
    }

    public static void replaceInSphere(Level level, BlockPos center, double radius, BlockState replaceTo) {
        fillSphere(level, center, radius, replaceTo, ($, state) -> !state.isAir());
    }

    public static void replaceInSphere(Level level, BlockPos center, double radius, BlockState replaceTo, Predicate<BlockPos> predicate) {
        fillSphere(level, center, radius, replaceTo, (pos, state) -> !state.isAir() && predicate.test(pos));
    }

    public static void replaceInSphere(Level level, BlockPos center, double radius, BlockState replaceTo, BiPredicate<BlockPos, BlockState> predicate) {
        fillSphere(level, center, radius, replaceTo, (pos, state) -> !state.isAir() && predicate.test(pos, state));
    }

    public static void fillSphere(Level level, BlockPos center, double radius, BlockState replaceTo) {
        fillSphere(level, center, radius, replaceTo, $ -> true);
    }

    public static void fillSphere(Level level, BlockPos center, double radius, BlockState replaceTo, Predicate<BlockPos> predicate) {
        for (double i = -radius; i <= radius; i++) {
            for (double j = -radius; j <= radius; j++) {
                for (double k = -radius; k <= radius; k++) {
                    if (i * i + j * j + k * k <= radius * radius) {
                        BlockPos pos = new BlockPos(center.getX() + i, center.getY() + j, center.getZ() + k);
                        if (predicate.test(pos)) {
                            level.setBlockAndUpdate(pos, replaceTo);
                        }
                    }
                }
            }
        }
    }

    public static void fillSphere(Level level, BlockPos center, double radius, BlockState replaceTo, BiPredicate<BlockPos, BlockState> predicate) {
        for (double i = -radius; i <= radius; i++) {
            for (double j = -radius; j <= radius; j++) {
                for (double k = -radius; k <= radius; k++) {
                    if (i * i + j * j + k * k <= radius * radius) {
                        BlockPos pos = new BlockPos(center.getX() + i, center.getY() + j, center.getZ() + k);
                        if (predicate.test(pos, level.getBlockState(pos))) {
                            level.setBlockAndUpdate(pos, replaceTo);
                        }
                    }
                }
            }
        }
    }

    public static void inRadius(Level level, BlockPos center, double radius, BiConsumer<Level, BlockPos> task) {
        for (double i = -radius; i <= radius; i++) {
            for (double j = -radius; j <= radius; j++) {
                for (double k = -radius; k <= radius; k++) {
                    if (i * i + j * j + k * k <= radius * radius) {
                        task.accept(level, new BlockPos(center.getX() + i, center.getY() + j, center.getZ() + k));
                    }
                }
            }
        }
    }

    public static void inRadiusCubic(Level level, BlockPos center, double radius, BiConsumer<Level, BlockPos> task) {
        for (double i = -radius; i <= radius; i++) {
            for (double j = -radius; j <= radius; j++) {
                for (double k = -radius; k <= radius; k++) {
                    task.accept(level, new BlockPos(center.getX() + i, center.getY() + j, center.getZ() + k));
                }
            }
        }
    }


}
