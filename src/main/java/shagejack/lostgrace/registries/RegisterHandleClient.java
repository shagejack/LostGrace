package shagejack.lostgrace.registries;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import shagejack.lostgrace.registries.block.BlockBuilder;

public class RegisterHandleClient {

    public static void init() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener((FMLClientSetupEvent event) -> BlockBuilder.setupRenderLayerTasks.forEach((task) -> task.get().run()));
    }
}
