package shagejack.lostgrace.compat.shimmer;

import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.light.LightManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import shagejack.lostgrace.foundation.compat.IModCompatContext;
import shagejack.lostgrace.registries.block.AllBlocks;

public class ShimmerModCompatEventSubscriber implements IModCompatContext {

    @SubscribeEvent
    public void onEvent(FMLClientSetupEvent event) {
        LightManager.INSTANCE.registerBlockLight(AllBlocks.grace.block().get(), (state, pos) -> new ColorPointLight.Template(1, 0x64FFD700));
    }

    @Override
    public void run() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onEvent);
    }
}
