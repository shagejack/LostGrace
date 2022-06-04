package shagejack.lostgrace.registries.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.event.RegistryEvent;
import shagejack.lostgrace.LostGrace;

import java.util.ArrayList;
import java.util.List;

public class AllPotions {

    public static final List<Binder> tasks = new ArrayList<>();

    public static final Potion SLEEP = register("sleep", new Potion(LostGrace.asKey("sleep"), new MobEffectInstance(AllMobEffects.SLEEP, 3600)));
    public static final Potion LONG_SLEEP = register("long_sleep", new Potion(LostGrace.asKey("sleep"), new MobEffectInstance(AllMobEffects.SLEEP, 9600)));
    public static final Potion STRONG_SLEEP = register("strong_sleep", new Potion(LostGrace.asKey("sleep"), new MobEffectInstance(AllMobEffects.SLEEP, 1800, 1)));

    public static Potion register(String key, Potion potion) {
        tasks.add(new Binder(key, potion));
        return potion;
    }

    public static void bind(final RegistryEvent.Register<Potion> event) {
        tasks.forEach(binder -> binder.register(event));
    }

    private record Binder(String key, Potion potion) {
        private void register(final RegistryEvent.Register<Potion> event) {
            potion.setRegistryName(LostGrace.asResource(key));
            event.getRegistry().register(potion);
        }
    }
}
