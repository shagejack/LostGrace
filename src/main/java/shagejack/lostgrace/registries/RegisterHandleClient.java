package shagejack.lostgrace.registries;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import shagejack.lostgrace.registries.block.BlockBuilder;
import shagejack.lostgrace.registries.entity.EntityBuilder;
import shagejack.lostgrace.registries.item.ItemPropertyOverridesRegistry;
import shagejack.lostgrace.registries.tile.TileEntityBuilder;

public class RegisterHandleClient {

    public static void init() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
            TileEntityBuilder.bind(event);
            EntityBuilder.bindRenderers(event);
        });

        modBus.addListener(BlockBuilder::registerColors);
        modBus.addListener((FMLClientSetupEvent event) -> BlockBuilder.setupRenderLayerTasks.forEach((task) -> task.get().run()));

        modBus.addListener(ItemPropertyOverridesRegistry::propertyOverrideRegistry);
    }
}
