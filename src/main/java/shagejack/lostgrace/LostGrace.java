package shagejack.lostgrace;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shagejack.lostgrace.compat.top.TOPModCompat;
import shagejack.lostgrace.foundation.compat.ModCompatLoader;
import shagejack.lostgrace.foundation.config.LostGraceConfig;
import shagejack.lostgrace.foundation.handler.TickManager;
import shagejack.lostgrace.foundation.utility.Constants;
import shagejack.lostgrace.registries.RegisterHandle;
import shagejack.lostgrace.registries.setup.ModCommonEventSetup;

@Mod(LostGrace.MOD_ID)
public class LostGrace {

    public static final String MOD_ID = "lostgrace";
    public static final String MOD_NAME = "Lost Grace";
    public static final Logger LOGGER = LogManager.getLogger(LostGrace.MOD_NAME);
    public static final boolean isDataGen = FMLLoader.getLaunchHandler().isData();
    public static final ModCompatLoader modCompatLoader = new ModCompatLoader();

    public LostGrace() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        try {

            if (LostGraceConfig.ENABLE_TOP_PLUGIN.get()) {
                LOGGER.info("TOP Plugin Loaded.");
                modCompatLoader.addModCompat(new TOPModCompat());
            }

            LOGGER.info("Registering...");
            RegisterHandle.regRegisters();

            LOGGER.info("Initializing register & register event listeners...");
            RegisterHandle.init();

            LOGGER.info("Setting up common event listeners...");
            ModCommonEventSetup.setup(modEventBus, forgeEventBus);

            LOGGER.info("Initializing Configuration...");
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LostGraceConfig.SPEC, "lostgrace.toml");
            Constants.init();

            LOGGER.info("Attaching Process Inter Mod Communication event...");
            modEventBus.addListener(this::processIMC);

        } catch (Exception e) {
            LOGGER.error("Mod Loading Failed", e);
            throw new RuntimeException();
        }

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LostGraceClient.onClient(modEventBus, forgeEventBus));

        TickManager.attachListeners(forgeEventBus);

    }

    @SubscribeEvent
    public void processIMC(InterModProcessEvent event) {
        event.getIMCStream().forEach((message) ->
        {

        });
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
    public static String asKey(String path) {
        return MOD_ID + "." + path;
    }
}
