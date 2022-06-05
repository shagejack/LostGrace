package shagejack.lostgrace.foundation.tile;

import net.minecraftforge.event.TickEvent;
import shagejack.lostgrace.foundation.handler.ITickHandler;

import java.util.EnumSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TileEntityLateInitializationHandler implements ITickHandler {

    private static final TileEntityLateInitializationHandler INSTANCE = new TileEntityLateInitializationHandler();

    public Queue<TileEntityLateInitializer<?>> initializers = new ConcurrentLinkedQueue<>();

    private TileEntityLateInitializationHandler() {}

    public static TileEntityLateInitializationHandler getInstance() {
        return INSTANCE;
    }

    public boolean offer(TileEntityLateInitializer<?> initializer) {
        // server side only
        if (initializer.getLevel().isClientSide())
            return false;

        return initializers.offer(initializer);
    }

    @Override
    public String getName() {
        return "TileEntity Late Initialization Handler";
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        initializers.removeIf(TileEntityLateInitializer::tick);
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
