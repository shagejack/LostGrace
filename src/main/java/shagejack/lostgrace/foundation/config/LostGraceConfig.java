package shagejack.lostgrace.foundation.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.awt.*;

public final class LostGraceConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> GRACE_FORCE_FIRST_PERSON_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Double> GRACE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Double> GRACE_TELEPORT_SELECTION_DEVIATION_DEGREE;
    public static final ForgeConfigSpec.ConfigValue<Double> GRACE_FOG_RADIUS;
    public static final ForgeConfigSpec.ConfigValue<Integer> GRACE_FOG_ALPHA;
    public static final ForgeConfigSpec.ConfigValue<Integer> GRACE_FOG_COLOR;

    static {
        BUILDER.push("Lost Grace Config");

        GRACE_FORCE_FIRST_PERSON_DISTANCE = BUILDER.comment("The player-grace distance mentioned below is which between the player foot and the position 1.1 blocks above the grace center.",
                "Player within this distance of graces will be forcibly changed to first person. This distance is also used to determine if a grace should be rendered in fog. Default: 5.5").define("Grace Force First Person Distance", 5.5D);
        GRACE_MAX_DISTANCE = BUILDER.comment("The max distance allowed to interact with grace and to render fog. Default: 3.5").define("Grace Max Distance", 3.5D);
        GRACE_TELEPORT_SELECTION_DEVIATION_DEGREE = BUILDER.comment("When the player is in fog, a grace will be selected if the included angle between player sight vector and the vector from player head to the grace is smaller than this angle expressed in degrees. Default: 5.0").define("Grace Teleport Selection Deviation Degree", 5.0D);
        GRACE_FOG_RADIUS = BUILDER.comment("The radius of the fog sphere which appears when player touches the grace. Default: 6.0").define("Grace Fog Radius", 6.0D);
        GRACE_FOG_ALPHA = BUILDER.comment("The integer alpha channel value which determines the max opacity of the fog when not teleporting. 255 means full opaque while 0 means full transparent. Default: 217",
                        "The fog fades in as its alpha channel value grows linearly from 0 to this value in 20 ticks. Using value greater than 255 is not recommended and will likely to cause unexpected problems.").define("Grace Fog Alpha", 217);
        GRACE_FOG_COLOR = BUILDER.comment("The integer ARGB color of the fog. Default: -4144960 (java.awt.Color.LIGHT_GRAY)").define("Grace Fog Color", Color.LIGHT_GRAY.getRGB());

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
