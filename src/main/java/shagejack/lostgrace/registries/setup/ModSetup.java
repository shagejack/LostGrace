package shagejack.lostgrace.registries.setup;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.IEventBus;
import shagejack.lostgrace.contents.grace.GlobalGraceSet;
import shagejack.lostgrace.contents.grace.PlayerGraceEventHandler;
import shagejack.lostgrace.contents.item.goldenSeed.GoldenSeed;
import shagejack.lostgrace.contents.item.goldenSeed.GoldenSeedEventHandler;

public class ModSetup {

    public static void setup(IEventBus modEventBus, IEventBus forgeEventBus) {
        forgeEventBus.addGenericListener(Entity.class, PlayerGraceEventHandler::attachCapability);
        forgeEventBus.addListener(PlayerGraceEventHandler::onPlayerCloned);
        forgeEventBus.addListener(PlayerGraceEventHandler::onRegisterCapabilities);
        forgeEventBus.addListener(PlayerGraceEventHandler::onPlayerRespawn);
        forgeEventBus.addListener(GlobalGraceSet::tickGrace);
        forgeEventBus.addListener(GoldenSeedEventHandler::onGoldenSeedSpawn);
    }

}
