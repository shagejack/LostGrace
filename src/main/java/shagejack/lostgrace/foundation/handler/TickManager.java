package shagejack.lostgrace.foundation.handler;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import shagejack.lostgrace.foundation.utility.ITickHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TickManager {

    public static final Map<TickEvent.Type, List<ITickHandler>> tickHandlers = new HashMap<>();

    static {
        for (TickEvent.Type type : TickEvent.Type.values()) {
            tickHandlers.put(type, new ArrayList<>());
        }
    }

    public static void attachListeners(IEventBus eventBus) {
        eventBus.addListener(TickManager::worldTick);
        eventBus.addListener(TickManager::serverTick);
        eventBus.addListener(TickManager::playerTick);
        eventBus.addListener(TickManager::renderTick);
        eventBus.addListener(TickManager::clientTick);
    }

    public static void register(ITickHandler handler) {
        for (TickEvent.Type type : handler.getHandledTypes()) {
            tickHandlers.get(type).add(handler);
        }
    }

    public static boolean unregister(ITickHandler handler) {
        boolean removed = true;
        for (TickEvent.Type type : handler.getHandledTypes()) {
            if (!tickHandlers.get(type).remove(handler)) {
                removed = false;
            }
        }
        return removed;
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event) {
        TickEvent.Phase phrase = event.phase;
        for (ITickHandler handler : tickHandlers.get(TickEvent.Type.WORLD)) {
            if(handler.shouldFire(phrase)) handler.tick(TickEvent.Type.WORLD, event.world);
        }
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        TickEvent.Phase phrase = event.phase;
        for (ITickHandler handler : tickHandlers.get(TickEvent.Type.SERVER)) {
            if(handler.shouldFire(phrase)) handler.tick(TickEvent.Type.SERVER);
        }
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        TickEvent.Phase phrase = event.phase;
        for (ITickHandler handler : tickHandlers.get(TickEvent.Type.CLIENT)) {
            if(handler.shouldFire(phrase)) handler.tick(TickEvent.Type.CLIENT);
        }
    }

    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        TickEvent.Phase phrase = event.phase;
        for (ITickHandler handler : tickHandlers.get(TickEvent.Type.RENDER)) {
            if(handler.shouldFire(phrase)) handler.tick(TickEvent.Type.RENDER, event.renderTickTime);
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        TickEvent.Phase phrase = event.phase;
        for (ITickHandler handler : tickHandlers.get(TickEvent.Type.PLAYER)) {
            if(handler.shouldFire(phrase)) handler.tick(TickEvent.Type.PLAYER, event.player, event.side);
        }
    }
}
