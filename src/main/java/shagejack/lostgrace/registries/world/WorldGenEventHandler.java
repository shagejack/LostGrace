package shagejack.lostgrace.registries.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import shagejack.lostgrace.foundation.config.LostGraceConfig;

public class WorldGenEventHandler {

    public static void onBiomeLoad(final BiomeLoadingEvent event) {
        BiomeGenerationSettingsBuilder builder = event.getGeneration();
        Biome.ClimateSettings climate = event.getClimate();
        ResourceLocation biomeName = event.getName();

        if (climate.temperature > 0.4F && climate.temperature < 1.0F) {
            if (LostGraceConfig.TRINA_LILY_GEN.get()) {
                builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AllFeatures.PATCH_TRINA_LILY);
            }
        }
    }

}
