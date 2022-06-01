package shagejack.lostgrace.foundation.utility;

import java.awt.*;
import java.util.Random;

public class ColorUtils {

    private static final Random RANDOM = new Random();

    private ColorUtils() {
        throw new IllegalStateException(this.getClass().toString() + "should not be instantiated as it's a utility class.");
    }

    public static float getRed(int color) {
        return (color >> 16 & 0xFF) / 255.0F;
    }

    public static float getGreen(int color) {
        return (color >> 8 & 0xFF) / 255.0F;
    }

    public static float getBlue(int color) {
        return (color & 0xFF) / 255.0F;
    }

    public static float getAlpha(int color) {
        return (color >> 24 & 0xFF) > 0 ? (color >> 24 & 0xFF) / 255.0F  : 1.0F;
    }

    public static Color getRandomColor(Random random) {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static Color getRandomColor() {
        return getRandomColor(RANDOM);
    }

}
