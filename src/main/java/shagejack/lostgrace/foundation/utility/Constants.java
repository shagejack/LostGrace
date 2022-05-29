package shagejack.lostgrace.foundation.utility;

import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.foundation.config.LostGraceConfig;

import java.awt.*;

public class Constants {
    // built-in value
    public static final double PLAYER_SIGHT_Y_OFFSET = 1.7D;
    public static final double GRACE_DISTANCE_Y_OFFSET = 1.6D;

    // configurable value
    public static double GRACE_FORCE_FIRST_PERSON_DISTANCE;
    public static double GRACE_MAX_DISTANCE;
    public static double GRACE_TELEPORT_SELECTION_DEVIATION_DEGREE;
    public static double GRACE_FOG_RADIUS;
    public static int GRACE_FOG_ALPHA;
    public static Color GRACE_FOG_COLOR;

    public static double IMPACT_EVENT_CHANCE;
    public static double IMPACT_MAX_RADIUS;

    public static void init() {
        GRACE_FORCE_FIRST_PERSON_DISTANCE = LostGraceConfig.GRACE_FORCE_FIRST_PERSON_DISTANCE.get();
        GRACE_MAX_DISTANCE = LostGraceConfig.GRACE_MAX_DISTANCE.get();
        GRACE_TELEPORT_SELECTION_DEVIATION_DEGREE = LostGraceConfig.GRACE_TELEPORT_SELECTION_DEVIATION_DEGREE.get();
        GRACE_FOG_RADIUS = LostGraceConfig.GRACE_FOG_RADIUS.get();
        GRACE_FOG_ALPHA = LostGraceConfig.GRACE_FOG_ALPHA.get();
        GRACE_FOG_COLOR = new Color(LostGraceConfig.GRACE_FOG_COLOR.get());
        IMPACT_EVENT_CHANCE = LostGraceConfig.IMPACT_EVENT_CHANCE.get();
        IMPACT_MAX_RADIUS = LostGraceConfig.IMPACT_MAX_RADIUS.get();

        LostGrace.LOGGER.info("Successfully initialized constants from config.");
    }
}
