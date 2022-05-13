package shagejack.lostgrace;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shagejack.lostgrace.foundation.handler.TickManager;
import shagejack.lostgrace.registries.RegisterHandle;
import shagejack.lostgrace.registries.setup.ModSetup;

import java.util.stream.Collectors;

@Mod(LostGrace.MOD_ID)
public class LostGrace {

    public static final String MOD_ID = "lostgrace";
    public static final String MOD_NAME = "Lost Grace";
    public static final Logger LOGGER = LogManager.getLogger(LostGrace.MOD_NAME);
    public static final boolean isDataGen = FMLLoader.getLaunchHandler().isData();

    public LostGrace() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        try {
            LOGGER.info("Registering...");
            RegisterHandle.RegRegisters();

            LOGGER.info("Initializing...");
            RegisterHandle.init();

            LOGGER.info("Setting up event listeners...");
            ModSetup.setup(modEventBus, forgeEventBus);

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
