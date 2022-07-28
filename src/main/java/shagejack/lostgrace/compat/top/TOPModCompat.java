package shagejack.lostgrace.compat.top;

import shagejack.lostgrace.foundation.compat.IModCompatContext;
import shagejack.lostgrace.foundation.compat.IModCompat;

import java.util.List;

public class TOPModCompat implements IModCompat {

    @Override
    public String getId() {
        return "theoneprobe";
    }

    @Override
    public List<IModCompatContext> createContext() {
        return List.of(new TOPModCompatContext());
    }

}
