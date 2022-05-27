package shagejack.lostgrace.compat.top;

import net.minecraftforge.fml.InterModComms;
import shagejack.lostgrace.foundation.compat.IModCompatContext;

public class TOPModCompatContext implements IModCompatContext {

    @Override
    public void run() {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPIntegration::new);
    }

}
