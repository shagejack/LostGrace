package shagejack.lostgrace.contents.effect.sleep;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import shagejack.lostgrace.registries.effect.AllMobEffects;

import java.util.Objects;

public class SleepEffectEventHandler {

    public static void checkBedExists(SleepingLocationCheckEvent event) {
        if (event.getEntityLiving().hasEffect(AllMobEffects.SLEEP)) {
            event.setResult(Event.Result.ALLOW);
        }
    }

    public static void hurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.hasEffect(AllMobEffects.SLEEP)) {
            if (event.getAmount() > 5 * Objects.requireNonNull(entity.getEffect(AllMobEffects.SLEEP)).getAmplifier()) {
                entity.removeEffect(AllMobEffects.SLEEP);
                entity.stopSleeping();
            }
        }
    }
}
