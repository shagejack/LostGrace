package shagejack.lostgrace.compat.shimmer;

import shagejack.lostgrace.foundation.compat.IModCompat;
import shagejack.lostgrace.foundation.compat.IModCompatContext;

import java.util.List;

public class ShimmerModCompat implements IModCompat {
    @Override
    public String getId() {
        return "shimmer";
    }

    @Override
    public List<IModCompatContext> createContext() {
        return List.of(new ShimmerModCompatEventSubscriber());
    }
}
