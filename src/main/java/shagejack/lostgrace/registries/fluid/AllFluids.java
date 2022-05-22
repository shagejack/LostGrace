package shagejack.lostgrace.registries.fluid;

import shagejack.lostgrace.registries.record.FluidPair;

public class AllFluids {

    public static final FluidPair sacredBlood
            = new FluidBuilder("sacred_blood")
            .density(1024)
            .viscosity(1024)
            .build();

    public static final FluidPair profaneBlood
            = new FluidBuilder("profane_blood")
            .density(1024)
            .viscosity(1024)
            .build();

}
