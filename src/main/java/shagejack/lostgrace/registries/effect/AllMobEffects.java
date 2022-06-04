package shagejack.lostgrace.registries.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.contents.effect.sleep.SleepEffect;
import shagejack.lostgrace.registries.RegisterHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class AllMobEffects {

    public static final List<Binder> tasks = new ArrayList<>();

    public static final MobEffect SLEEP = register("sleep", new SleepEffect());

    public static MobEffect register(String key, MobEffect effect) {
        tasks.add(new Binder(key, effect));
        return effect;
    }

    public static void bind(final RegistryEvent.Register<MobEffect> event) {
        tasks.forEach(binder -> binder.register(event));
    }

    private record Binder(String key, MobEffect effect) {
        private void register(final RegistryEvent.Register<MobEffect> event) {
            effect.setRegistryName(LostGrace.asResource(key));
            event.getRegistry().register(effect);
        }
    }
}
