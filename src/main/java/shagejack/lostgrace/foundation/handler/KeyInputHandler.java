package shagejack.lostgrace.foundation.handler;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import shagejack.lostgrace.registries.AllKeys;

public class KeyInputHandler {

    public static boolean IS_GRACE_TELEPORT_KEY_DOWN = false;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        IS_GRACE_TELEPORT_KEY_DOWN = AllKeys.graceTeleportKeyMapping.isDown();
    }
}
