package shagejack.lostgrace.registries.setup;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import shagejack.lostgrace.LostGraceClient;
import shagejack.lostgrace.contents.block.grace.GraceUIHandler;
import shagejack.lostgrace.foundation.handler.KeyInputHandler;
import shagejack.lostgrace.foundation.handler.TickManager;

public class ModClientEventSetup {

    public static void setup(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(LostGraceClient::clientInit);
        modEventBus.addListener(LostGraceClient::onTextureStitch);

        TickManager.register(GraceUIHandler.getInstance());

        forgeEventBus.addListener(EventPriority.LOW, GraceUIHandler.getInstance()::render);
        forgeEventBus.addListener(GraceUIHandler.getInstance()::interact);
        forgeEventBus.addListener(KeyInputHandler::onKeyInput);
    }
}
