package shagejack.lostgrace;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import shagejack.lostgrace.contents.block.grace.GraceUIHandler;
import shagejack.lostgrace.foundation.handler.KeyInputHandler;
import shagejack.lostgrace.foundation.handler.TickManager;
import shagejack.lostgrace.registries.AllKeys;
import shagejack.lostgrace.registries.AllTextures;
import shagejack.lostgrace.registries.RegisterHandleClient;

public class LostGraceClient {

    public static void onClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        RegisterHandleClient.init();

        LostGrace.LOGGER.info("Initializing Client...");

        modEventBus.addListener(LostGraceClient::clientInit);
        modEventBus.addListener(LostGraceClient::onTextureStitch);

        TickManager.register(GraceUIHandler.getInstance());

        forgeEventBus.addListener(EventPriority.LOW, GraceUIHandler.getInstance()::render);
        forgeEventBus.addListener(GraceUIHandler.getInstance()::interact);
        forgeEventBus.addListener(KeyInputHandler::onKeyInput);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        AllKeys.init();
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return;
        }
        event.addSprite(AllTextures.HUMANITY);
        event.addSprite(AllTextures.GUIDANCE);
        event.addSprite(AllTextures.IMPACT_CROSS);
        event.addSprite(AllTextures.IMPACT_RING);
    }
}
