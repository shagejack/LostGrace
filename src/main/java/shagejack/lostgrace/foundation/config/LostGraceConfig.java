package shagejack.lostgrace.foundation.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.awt.*;

public final class LostGraceConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final String CATEGORY_COMPAT = "compatibility";
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_TOP_PLUGIN;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_JEI_PLUGIN;

    public static final String CATEGORY_WORLDGEN = "worldgen";
    public static final ForgeConfigSpec.ConfigValue<Boolean> GENERATE_CHEST_LOOT;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TRINA_LILY_GEN;
    public static final ForgeConfigSpec.ConfigValue<Integer> TRINA_LILY_GEN_IN_TRIES;

    public static final String CATEGORY_GRACE = "grace";
    public static final ForgeConfigSpec.ConfigValue<Double> GRACE_FORCE_FIRST_PERSON_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Double> GRACE_MAX_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<Double> GRACE_TELEPORT_SELECTION_DEVIATION_DEGREE;
    public static final ForgeConfigSpec.ConfigValue<Double> GRACE_FOG_RADIUS;
    public static final ForgeConfigSpec.ConfigValue<Integer> GRACE_FOG_ALPHA;
    public static final ForgeConfigSpec.ConfigValue<Integer> GRACE_FOG_COLOR;

    public static final String CATEGORY_DREAM_POOL = "dreampool";
    public static final ForgeConfigSpec.ConfigValue<Integer> TICK_PER_THOUSANDTH;

    public static final String CATEGORY_IMPACT = "impact";
    public static final ForgeConfigSpec.ConfigValue<Double> IMPACT_EVENT_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Double> IMPACT_MAX_RADIUS;

    static {
        BUILDER.comment("Mod Compatibility").push(CATEGORY_COMPAT);
        ENABLE_TOP_PLUGIN = BUILDER.comment("Enable TOP Plugin").define("enableTOPPlugin", true);
        ENABLE_JEI_PLUGIN = BUILDER.comment("Enable JEI Plugin").define("enableJEIPlugin", true);
        BUILDER.pop();

        BUILDER.comment("World Generation").push(CATEGORY_WORLDGEN);
        GENERATE_CHEST_LOOT = BUILDER.comment("Generate Chest Loot (Default: true)", "It's not recommended to disable this without further modifications as the only way for players to get some items is from chest loots.").define("generateChestLoot", true);
        TRINA_LILY_GEN = BUILDER.comment("Generate Trina's Lily on temperate biomes (temperature between 0.4 and 0.9). Default: true").define("trinaLilyGen", true);
        TRINA_LILY_GEN_IN_TRIES = BUILDER.comment("How much tries needed on average to generate Trina's Lily once. Smaller value means generating more frequently. Default: 120").defineInRange("trinaLilyGenInTries", 120, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.comment("Grace Settings").push(CATEGORY_GRACE);
        GRACE_FORCE_FIRST_PERSON_DISTANCE = BUILDER.comment("The player-grace distance mentioned below is which between the player foot and the position 1.1 blocks above the grace center.",
                "Player within this distance of graces will be forcibly changed to first person. This distance is also used to determine if a grace should be rendered in fog. Default: 5.5").define("graceForceFirstPersonDistance", 5.5D);
        GRACE_MAX_DISTANCE = BUILDER.comment("The max distance allowed to interact with grace and to render fog. Default: 3.5").define("graceMaxDistance", 3.5D);
        GRACE_TELEPORT_SELECTION_DEVIATION_DEGREE = BUILDER.comment("When the player is in fog, a grace will be selected if the included angle between player sight vector and the vector from player head to the grace is smaller than this angle expressed in degrees. Default: 3.0").define("Grace Teleport Selection Deviation Degree", 3.0D);
        GRACE_FOG_RADIUS = BUILDER.comment("The radius of the fog sphere which appears when player touches the grace. Default: 6.0").define("graceFogRadius", 6.0D);
        GRACE_FOG_ALPHA = BUILDER.comment("The integer alpha channel value which determines the max opacity of the fog when not teleporting. 255 means full opaque while 0 means full transparent. Default: 217",
                        "The fog fades in as its alpha channel value grows linearly from 0 to this value in 20 ticks. Using value greater than 255 is not recommended and will likely to cause unexpected problems.").define("graceFogAlpha", 217);
        GRACE_FOG_COLOR = BUILDER.comment("The integer ARGB color of the fog. Default: -4144960 (java.awt.Color.LIGHT_GRAY)").define("graceFogColor", Color.LIGHT_GRAY.getRGB());
        BUILDER.pop();

        BUILDER.comment("Dream Pool").push(CATEGORY_DREAM_POOL);
        TICK_PER_THOUSANDTH = BUILDER.comment("How many ticks will dream pool costs to process one thousandth of a broken dream. Default: 10").defineInRange("tickPerThousandth", 10, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.comment("Impact Event").push(CATEGORY_IMPACT);
        IMPACT_EVENT_CHANCE = BUILDER.comment("The chance that Impact Event will happen for some reason... Default: 0.1").define("impactEventChance", 0.1D);
        IMPACT_MAX_RADIUS = BUILDER.comment("The max influence radius of Impact Event. Default: 75.0").define("impactMaxRadius", 75.0D);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
