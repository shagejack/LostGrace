package shagejack.lostgrace;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import shagejack.lostgrace.registries.AllKeys;
import shagejack.lostgrace.registries.AllTextures;
import shagejack.lostgrace.registries.RegisterHandleClient;
import shagejack.lostgrace.registries.setup.ModClientEventSetup;

public class LostGraceClient {

    public static void onClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        LostGrace.LOGGER.info("Initializing Client...");

        LostGrace.LOGGER.info("Initializing Client Register Events...");
        RegisterHandleClient.init();

        LostGrace.LOGGER.info("Initializing Client Events...");
        ModClientEventSetup.setup(modEventBus, forgeEventBus);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        LostGrace.LOGGER.info("Registering Key Bindings...");
        AllKeys.init();
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return;
        }

        LostGrace.LOGGER.info("Stitching Texture...");
        event.addSprite(AllTextures.HUMANITY);
        event.addSprite(AllTextures.GUIDANCE);
        event.addSprite(AllTextures.IMPACT_CROSS);
        event.addSprite(AllTextures.IMPACT_RING);
    }
}
