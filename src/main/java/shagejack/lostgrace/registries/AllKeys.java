package shagejack.lostgrace.registries;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;

public class AllKeys {

    public static final String KEY_CATEGORIES_LOSTGRACE = "key.categories.lostgrace";

    public static final String KEY_GRACETELEPORT = "key.graceteleport";

    public static KeyMapping graceTeleportKeyMapping;

    public static void init() {
        graceTeleportKeyMapping = new KeyMapping(KEY_GRACETELEPORT, KeyConflictContext.IN_GAME, InputConstants.getKey("key.keyboard.v"), KEY_CATEGORIES_LOSTGRACE);
        ClientRegistry.registerKeyBinding(graceTeleportKeyMapping);
    }

}
