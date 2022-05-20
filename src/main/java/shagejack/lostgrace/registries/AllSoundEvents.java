package shagejack.lostgrace.registries;

import net.minecraft.sounds.SoundEvent;
import shagejack.lostgrace.LostGrace;

public class AllSoundEvents {

    public static SoundEvent DIES_IRAE = create("dies_irae");
    public static SoundEvent KAPPA = create("kappa");

    private static SoundEvent create(String name) {
        SoundEvent event = new SoundEvent(LostGrace.asResource(name));
        RegisterHandle.SOUND_EVENT_REGISTER.register(name, () -> event);
        return event;
    }
}
