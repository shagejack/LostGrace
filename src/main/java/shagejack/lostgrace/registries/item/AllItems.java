package shagejack.lostgrace.registries.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.contents.item.blackKnife.BlackKnifeItem;
import shagejack.lostgrace.contents.item.blood.ScabItem;
import shagejack.lostgrace.contents.item.goldenSeed.GoldenSeedItem;
import shagejack.lostgrace.contents.item.memoryOfGrace.MemoryOfGraceItem;
import shagejack.lostgrace.contents.item.record.DiesIraeRecordItem;
import shagejack.lostgrace.contents.item.record.KappaRecordItem;
import shagejack.lostgrace.registries.AllTabs;
import shagejack.lostgrace.registries.fluid.AllFluids;

import static net.minecraft.world.item.Items.BUCKET;

public class AllItems {

    public static final RegistryObject<Item> memoryOfGrace = new ItemBuilder()
            .properties(properties -> properties.stacksTo(1))
            .name("memory_of_grace")
            .build(MemoryOfGraceItem::new);

    public static final RegistryObject<Item> goldenSeed = new ItemBuilder()
            .properties(properties -> properties.stacksTo(1).fireResistant())
            .name("golden_seed")
            .build(GoldenSeedItem::new);

    public static final RegistryObject<Item> blackKnife = new ItemBuilder()
            .properties(properties -> properties.stacksTo(1).fireResistant().rarity(Rarity.EPIC).setNoRepair())
            .name("black_knife")
            .build(BlackKnifeItem::new);

    public static final RegistryObject<Item> profaneBloodBucket = new ItemBuilder()
            .properties(properties -> properties.stacksTo(1).craftRemainder(BUCKET))
            .name("profane_blood_bucket")
            .build(properties -> new BucketItem(AllFluids.profaneBlood.asFluidSupplier(), properties));

    public static final RegistryObject<Item> sacredBloodBucket = new ItemBuilder()
            .properties(properties -> properties.stacksTo(1).craftRemainder(BUCKET))
            .noTab()
            .name("sacred_blood_bucket")
            .build(properties -> new BucketItem(AllFluids.sacredBlood.asFluidSupplier(), properties));

    public static final RegistryObject<Item> scab = new ItemBuilder()
            .properties(properties -> properties.food(new FoodProperties.Builder().alwaysEat().fast().nutrition(1).saturationMod(0.5f)
                    .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.1f)
                    .effect(() -> new MobEffectInstance(MobEffects.HEAL, 1, 0), 0.35f)
                    .effect(() -> new MobEffectInstance(MobEffects.HARM, 1, 0), 0.05f)
                    .effect(() -> new MobEffectInstance(MobEffects.POISON, 40, 0), 0.02f)
                    .effect(() -> new MobEffectInstance(MobEffects.WITHER, 40, 0), 0.01f)
                    .build())
            )
            .name("scab")
            .build(ScabItem::new);

    public static final RegistryObject<Item> musicDiscDiesIrae = new ItemBuilder()
            .properties(properties -> properties.stacksTo(1).tab(AllTabs.tabMain).rarity(Rarity.EPIC))
            .name("music_disc_dies_irae")
            .build(DiesIraeRecordItem::new);

    public static final RegistryObject<Item> musicDiscKappa = new ItemBuilder()
            .properties(properties -> properties.stacksTo(1).tab(AllTabs.tabMain).rarity(Rarity.EPIC))
            .name("music_disc_kappa")
            .build(KappaRecordItem::new);
}
