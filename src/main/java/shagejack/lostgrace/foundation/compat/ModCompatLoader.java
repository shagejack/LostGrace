package shagejack.lostgrace.foundation.compat;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.ModList;
import org.apache.commons.compress.utils.Sets;

import java.util.List;
import java.util.Set;

public class ModCompatLoader {

    protected final List<IModCompat> compat = Lists.newLinkedList();

    public void addModCompat(IModCompat modCompat) {
        if(shouldLoadCompat(modCompat)) {
            modCompat.createContext().run();
        }
    }

    public boolean shouldLoadCompat(IModCompat modCompat) {
        return isModLoaded(modCompat);
    }

    private boolean isModLoaded(IModCompat modCompat) {
        return ModList.get().isLoaded(modCompat.getId());
    }


}
