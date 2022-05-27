package shagejack.lostgrace.foundation.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LevelUtils {

    private LevelUtils() {
        throw new IllegalStateException(this.getClass().toString() + "should not be instantiated as it's a utility class.");
    }

    public static void replaceInSphere(Level level, Vector3 center, double radius, BlockState replaceTo) {
        replaceInSphere(level, center, radius, replaceTo, 1.0);
    }

    public static void replaceInSphere(Level level, Vector3 center, double radius, BlockState replaceTo, double chance) {
        for (int i = -(int)radius; i < radius + 1; i++) {
            for (int j = -(int)radius; j < radius + 1; j++) {
                for (int k = -(int)radius; k < radius + 1; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (Vector3.atCenterOf(pos).distance(center) <= radius && level.getRandom().nextDouble() < chance) {
                        level.setBlockAndUpdate(pos, replaceTo);
                    }
                }
            }
        }
    }

}
