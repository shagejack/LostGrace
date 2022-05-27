package shagejack.lostgrace.compat.top;

import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

public class TOPIntegration implements Function<ITheOneProbe, Void> {

    @Override
    public Void apply(ITheOneProbe probe) {
        probe.registerProvider(new TOPData());
        return null;
    }
}
