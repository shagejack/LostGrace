package shagejack.lostgrace.foundation.utility;

import net.minecraft.core.BlockPos;

import java.util.Random;

public class MathUtils {

    private MathUtils() {
        throw new IllegalStateException(this.getClass().toString() + "should not be instantiated as it's a utility class.");
    }

    public static double lerp(double delta) {
        return 2 * delta - 1;
    }

    public static double lerp(double delta, double from, double to) {
        return from + delta * (to - from);
    }

    public static BlockPos getBetweenClosedBlockPos(Vector3 toParse, Vector3 another) {
        return new BlockPos(betweenClosedInt(toParse.x(), another.x()), betweenClosedInt(toParse.y(), another.y()), betweenClosedInt(toParse.z(), another.z()));
    }

    public static int betweenClosedInt(double toParse, double another) {
        return (int) (toParse > another ? Math.floor(toParse) : Math.ceil(toParse));
    }

    public static BlockPos randomBetweenBlockPos(Random random, BlockPos a, BlockPos b) {
        return new BlockPos(randomBetweenInt(random, a.getX(), b.getX()), randomBetweenInt(random, a.getY(), b.getY()), randomBetweenInt(random, a.getZ(), b.getZ()));
    }

    public static int randomBetweenInt(Random random, int aInclusive, int bInclusive) {
        if (aInclusive < bInclusive) {
            return random.nextInt(aInclusive, bInclusive + 1);
        } else {
            return random.nextInt(bInclusive, aInclusive + 1);
        }
    }

}
