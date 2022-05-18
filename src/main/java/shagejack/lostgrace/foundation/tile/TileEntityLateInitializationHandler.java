package shagejack.lostgrace.foundation.tile;

import net.minecraftforge.event.TickEvent;
import shagejack.lostgrace.foundation.utility.ITickHandler;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TileEntityLateInitializationHandler implements ITickHandler {

    private static final TileEntityLateInitializationHandler INSTANCE = new TileEntityLateInitializationHandler();

    public List<TileEntityLateInitializer<?>> initializers = new ArrayList<>();

    private TileEntityLateInitializationHandler() {}

    public static TileEntityLateInitializationHandler getInstance() {
        return INSTANCE;
    }

    public boolean add(TileEntityLateInitializer<?> initializer) {
        return initializers.add(initializer);
    }

    @Override
    public String getName() {
        return "TileEntity Late Initialization Handler";
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        int index = 0;
        int size = initializers.size();
        while (index < size) {
            if (initializers.get(index).tick()) {
                initializers.remove(index);
                size--;
            } else {
                index++;
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.SERVER);
    }

    @Override
    public boolean shouldFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }
}
