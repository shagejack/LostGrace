package shagejack.lostgrace.registries.setup;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import shagejack.lostgrace.contents.grace.GlobalGraceSet;
import shagejack.lostgrace.contents.grace.PlayerGraceEventHandler;
import shagejack.lostgrace.contents.item.blackKnife.BlackKnifeEventHandler;
import shagejack.lostgrace.contents.item.goldenSeed.GoldenSeedEventHandler;
import shagejack.lostgrace.contents.recipe.anvil.AnvilLifeCycle;
import shagejack.lostgrace.foundation.handler.TickManager;
import shagejack.lostgrace.foundation.tile.TileEntityLateInitializationHandler;
import shagejack.lostgrace.registries.AllCommands;
import shagejack.lostgrace.registries.item.ItemPropertyOverridesRegistry;
import shagejack.lostgrace.registries.recipe.AllRecipeTypes;

public class ModSetup {

    public static void setup(IEventBus modEventBus, IEventBus forgeEventBus) {
        forgeEventBus.addGenericListener(Entity.class, PlayerGraceEventHandler::attachCapability);

        forgeEventBus.addListener(AllCommands::registerCommand);

        forgeEventBus.addListener(PlayerGraceEventHandler::onPlayerCloned);
        forgeEventBus.addListener(PlayerGraceEventHandler::onRegisterCapabilities);
        forgeEventBus.addListener(PlayerGraceEventHandler::onPlayerRespawn);

        forgeEventBus.addListener(GlobalGraceSet::tickGrace);
        forgeEventBus.addListener(GlobalGraceSet::onDataLoad);
        forgeEventBus.addListener(GlobalGraceSet::onDataSave);

        forgeEventBus.addListener(GoldenSeedEventHandler::onGoldenSeedSpawn);
        forgeEventBus.addListener(BlackKnifeEventHandler::onBlackKnifeDropOnFloor);

        forgeEventBus.addListener(AnvilLifeCycle::onAnvilFall);
        forgeEventBus.addListener(AnvilLifeCycle::serverTick);

        modEventBus.addGenericListener(RecipeSerializer.class, AllRecipeTypes::register);
        modEventBus.addListener(ItemPropertyOverridesRegistry::propertyOverrideRegistry);

        TickManager.register(TileEntityLateInitializationHandler.getInstance());
    }

}
