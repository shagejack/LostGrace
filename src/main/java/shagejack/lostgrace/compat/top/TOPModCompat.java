package shagejack.lostgrace.compat.top;

import shagejack.lostgrace.foundation.compat.IModCompatContext;
import shagejack.lostgrace.foundation.compat.IModCompat;

public class TOPModCompat implements IModCompat {

    @Override
    public String getId() {
        return "theoneprobe";
    }

    @Override
    public IModCompatContext createContext() {
        return new TOPModCompatContext();
    }

}
