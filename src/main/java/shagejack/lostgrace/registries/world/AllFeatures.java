package shagejack.lostgrace.registries.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.foundation.config.LostGraceConfig;
import shagejack.lostgrace.registries.block.AllBlocks;

import java.util.List;

import static shagejack.lostgrace.registries.RegisterHandle.FEATURE_REGISTER;

public class AllFeatures {

    public static final BlockPos BELOW = new BlockPos(0, -1, 0);

    public static Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> FEATURE_PATCH_TRINA_LILY;

    public static Holder<PlacedFeature> PATCH_TRINA_LILY;

    static {

        FEATURE_PATCH_TRINA_LILY = register(LostGrace.asResource("patch_trina_lily"), Feature.RANDOM_PATCH,
                randomPatchConfiguration(AllBlocks.trinaLily.block().get(), 64, 4, BlockPredicate.matchesTag(BlockTags.DIRT, BELOW))
        );

        PATCH_TRINA_LILY = register(LostGrace.asResource("patch_trina_lily"), FEATURE_PATCH_TRINA_LILY,
                RarityFilter.onAverageOnceEvery(LostGraceConfig.TRINA_LILY_GEN_IN_TRIES.get()),
                InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome()
        );

    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC, ?>> register(ResourceLocation id, F feature, FC featureConfiguration) {
        return register(BuiltinRegistries.CONFIGURED_FEATURE, id, new ConfiguredFeature<>(feature, featureConfiguration));
    }

    private static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> register(ResourceLocation id, Feature<NoneFeatureConfiguration> feature) {
        return register(id, feature, FeatureConfiguration.NONE);
    }

    public static Holder<PlacedFeature> register(ResourceLocation id, Holder<? extends ConfiguredFeature<?, ?>> feature, PlacementModifier... placementModifiers) {
        return register(id, feature, List.of(placementModifiers));
    }

    public static Holder<PlacedFeature> register(ResourceLocation id, Holder<? extends ConfiguredFeature<?, ?>> feature, List<PlacementModifier> placementModifiers) {
        return register(BuiltinRegistries.PLACED_FEATURE, id, new PlacedFeature(Holder.hackyErase(feature), List.copyOf(placementModifiers)));
    }

    private static <V extends T, T> Holder<V> register(Registry<T> registry, ResourceLocation id, V value) {
        return (Holder<V>) BuiltinRegistries.register(registry, id, value);
    }

    public static RandomPatchConfiguration randomPatchConfiguration(Block block, int tries, int xzSpread, BlockPredicate plantedOn) {
        return new RandomPatchConfiguration(tries, xzSpread, 3, PlacementUtils.filtered(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(block)),
                BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, plantedOn)
        ));
    }

    protected static class FeatureBuilder<T extends Feature<?>> {
        private String name;
        public T feature;

        FeatureBuilder<T> name(String name) {
            this.name = name;
            return this;
        }

        public FeatureBuilder(T feature) {
            this.feature = feature;
        }

        T build() {
            FEATURE_REGISTER.register(name, () -> feature);
            return feature;
        }
    }
}
