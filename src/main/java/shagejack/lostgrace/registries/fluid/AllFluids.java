package shagejack.lostgrace.registries.fluid;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.LiquidBlock;
import shagejack.lostgrace.registries.block.AllBlocks;
import shagejack.lostgrace.registries.item.AllItems;
import shagejack.lostgrace.registries.record.FluidPair;

public class AllFluids {

    public static final FluidPair sacredBlood
            = new FluidBuilder("sacred_blood")
            .tags(FluidTags.WATER)
            .density(1050)
            .viscosity(5000)
            .tickRate(24)
            // this is intended
            .bucketItem(AllItems.profaneBloodBucket)
            .liquidBlock(() -> (LiquidBlock) AllBlocks.sacredBlood.get())
            .build();

    public static final FluidPair profaneBlood
            = new FluidBuilder("profane_blood")
            .tags(FluidTags.WATER)
            .density(1060)
            .viscosity(5050)
            .tickRate(25)
            .bucketItem(AllItems.profaneBloodBucket)
            .liquidBlock(() -> (LiquidBlock) AllBlocks.profaneBlood.get())
            .build();

    public static final FluidPair dream
            = new FluidBuilder("dream")
            .tags(FluidTags.WATER)
            .density(0)
            .viscosity(0)
            .tickRate(1)
            .bucketItem(AllItems.dreamBucket)
            .liquidBlock(() -> (LiquidBlock) AllBlocks.dream.get())
            .build();

}
