package shagejack.lostgrace;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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
import shagejack.lostgrace.registries.setup.ModSetup;

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

            modCompatLoader.addModCompat(new TOPModCompat());

            LOGGER.info("Registering...");
            RegisterHandle.RegRegisters();

            LOGGER.info("Initializing...");
            RegisterHandle.init();

            LOGGER.info("Setting up event listeners...");
            ModSetup.setup(modEventBus, forgeEventBus);

            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LostGraceConfig.SPEC, "lostgrace.toml");

            Constants.init();
        } catch (Exception e) {
            LOGGER.error(e);
            throw new RuntimeException();
        }

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LostGraceClient.onClient(modEventBus, forgeEventBus));

        TickManager.attachListeners(forgeEventBus);
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
