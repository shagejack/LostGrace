package shagejack.lostgrace.registries;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import shagejack.lostgrace.registries.loot.LootModifiersEventHandler;
import shagejack.lostgrace.foundation.network.AllPackets;
import shagejack.lostgrace.registries.block.AllBlocks;
import shagejack.lostgrace.registries.block.BlockBuilder;
import shagejack.lostgrace.registries.effect.AllMobEffects;
import shagejack.lostgrace.registries.effect.AllPotions;
import shagejack.lostgrace.registries.entity.AllEntityTypes;
import shagejack.lostgrace.registries.entity.EntityBuilder;
import shagejack.lostgrace.registries.fluid.AllFluids;
import shagejack.lostgrace.registries.item.AllItems;
import shagejack.lostgrace.registries.recipe.AllRecipeTypes;
import shagejack.lostgrace.registries.recipe.vanilla.AllBrewingRecipes;
import shagejack.lostgrace.registries.tile.AllTileEntities;
import shagejack.lostgrace.registries.tile.TileEntityBuilder;
import shagejack.lostgrace.registries.world.AllFeatures;

import static net.minecraftforge.registries.ForgeRegistries.*;
import static shagejack.lostgrace.LostGrace.MOD_ID;

public class RegisterHandle {
    public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ITEMS, MOD_ID);
    public static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(BLOCKS, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTER = DeferredRegister.create(BLOCK_ENTITIES, MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTER = DeferredRegister.create(CONTAINERS, MOD_ID);
    public static final DeferredRegister<Fluid> FLUID_REGISTER = DeferredRegister.create(FLUIDS, MOD_ID);
    public static final DeferredRegister<Feature<?>> FEATURE_REGISTER = DeferredRegister.create(FEATURES, MOD_ID);
    public static final DeferredRegister<Potion> POTION_REGISTER = DeferredRegister.create(POTIONS, MOD_ID);
    public static final DeferredRegister<Enchantment> ENCHANTMENT_REGISTER = DeferredRegister.create(ENCHANTMENTS, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE_REGISTER = DeferredRegister.create(ENTITIES, MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENT_REGISTER = DeferredRegister.create(SOUND_EVENTS, MOD_ID);

    public static void init() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        new AllPotions();
        new AllItems();
        new AllBlocks();
        new AllFluids();
        new AllTileEntities();
        new AllEntityTypes();
        new AllSoundEvents();

        modBus.addGenericListener(MobEffect.class, AllMobEffects::bind);
        modBus.addGenericListener(Potion.class, AllPotions::bind);
        modBus.addListener((FMLCommonSetupEvent event) -> AllBrewingRecipes.init());
        modBus.addListener(BlockBuilder::registerColors);

        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
            TileEntityBuilder.bind(event);
            EntityBuilder.bindRenderers(event);
        });

        modBus.addListener((EntityAttributeCreationEvent event) -> EntityBuilder.bindAttributes(event));

        modBus.addGenericListener(RecipeSerializer.class, AllRecipeTypes::register);
        modBus.addGenericListener(GlobalLootModifierSerializer.class, LootModifiersEventHandler::registerLootModifiers);

        AllPackets.registerPackets();
    }

    public static void RegRegisters() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEM_REGISTER.register(bus);
        BLOCK_REGISTER.register(bus);
        BLOCK_ENTITY_TYPE_REGISTER.register(bus);
        MENU_TYPE_REGISTER.register(bus);
        FLUID_REGISTER.register(bus);
        FEATURE_REGISTER.register(bus);
        POTION_REGISTER.register(bus);
        ENCHANTMENT_REGISTER.register(bus);
        ENTITY_TYPE_REGISTER.register(bus);
        SOUND_EVENT_REGISTER.register(bus);
    }
}
