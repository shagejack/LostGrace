package shagejack.lostgrace;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import shagejack.lostgrace.contents.block.grace.GraceRenderer;

public class LostGraceClient {
    // public static final ModelSwapper MODEL_SWAPPER = new ModelSwapper();

    public static void onClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        LostGrace.LOGGER.info("Initializing Client...");

        modEventBus.addListener(LostGraceClient::clientInit);
        modEventBus.addListener(LostGraceClient::onTextureStitch);

        // MODEL_SWAPPER.registerListeners(modEventBus);

    }

    public static void clientInit(final FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return;
        }
        event.addSprite(GraceRenderer.HUMANITY);
    }
}
