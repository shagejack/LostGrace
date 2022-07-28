package shagejack.lostgrace.foundation.compat;

import java.util.List;

public interface IModCompat {

    String getId();

    List<IModCompatContext> createContext();
}
