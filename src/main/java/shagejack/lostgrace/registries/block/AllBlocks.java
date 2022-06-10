package shagejack.lostgrace.registries.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.contents.block.blood.BloodLiquidBlock;
import shagejack.lostgrace.contents.block.bloodAltar.BloodAltarBlock;
import shagejack.lostgrace.contents.block.dreamPool.DreamPoolBlock;
import shagejack.lostgrace.contents.block.fresh.FreshBlock;
import shagejack.lostgrace.contents.block.fresh.RottenFreshBlock;
import shagejack.lostgrace.contents.block.grace.GraceBlock;
import shagejack.lostgrace.contents.block.runeStone.RuneStoneBlock;
import shagejack.lostgrace.contents.block.spell.ChalkRuneBlock;
import shagejack.lostgrace.contents.block.trinaLily.TrinaLilyBlock;
import shagejack.lostgrace.registries.fluid.AllFluids;
import shagejack.lostgrace.registries.item.ItemBuilder;
import shagejack.lostgrace.registries.record.ItemBlock;

public class AllBlocks {

    public static final ItemBlock grace = new BlockBuilder()
            .name("grace")
            .properties(properties -> properties.lightLevel(state -> 15).noCollission())
            .buildBlock(GraceBlock::new)
            .buildItem(ItemBuilder::noTab);

    public static final ItemBlock bloodAltar = new BlockBuilder()
            .name("blood_altar")
            .properties(properties -> properties.strength(2.5f, 3600000.0f))
            .buildBlock(BloodAltarBlock::new)
            .buildItem();

    public static final ItemBlock dreamPool = new BlockBuilder()
            .name("dream_pool")
            .properties(properties -> properties)
            .buildBlock(DreamPoolBlock::new)
            .buildItem();

    public static final ItemBlock fresh = new BlockBuilder()
            .name("fresh")
            .material(Material.CLAY)
            .properties(properties -> properties.sound(SoundType.SLIME_BLOCK))
            .buildBlock(FreshBlock::new)
            .buildItem();

    public static final ItemBlock rottenFresh = new BlockBuilder()
            .name("rotten_fresh")
            .material(Material.CLAY)
            .properties(properties -> properties.sound(SoundType.SLIME_BLOCK))
            .buildBlock(RottenFreshBlock::new)
            .buildItem();

    public static final ItemBlock runeStone = new BlockBuilder()
            .name("rune_stone")
            .material(Material.STONE)
            .properties(properties -> properties)
            .buildBlock(RuneStoneBlock::new)
            .buildItem();

    public static final RegistryObject<Block> sacredBlood = new BlockBuilder()
            .name("sacred_blood")
            .material(Material.WATER)
            .properties(properties -> properties.noCollission().strength(100.0F).noDrops())
            .buildBlock(properties -> new BloodLiquidBlock(AllFluids.sacredBlood.still(), properties))
            .noItem();

    public static final RegistryObject<Block> profaneBlood = new BlockBuilder()
            .name("profane_blood")
            .material(Material.WATER)
            .properties(properties -> properties.noCollission().strength(100.0F).noDrops())
            .buildBlock(properties -> new BloodLiquidBlock(AllFluids.profaneBlood.still(), properties))
            .noItem();

    public static final RegistryObject<Block> dream = new BlockBuilder()
            .name("dream")
            .material(Material.WATER)
            .properties(properties -> properties.noCollission().strength(100.0F).noDrops())
            .buildBlock(properties -> new LiquidBlock(AllFluids.dream.still(), properties))
            .noItem();

    public static final ItemBlock trinaLily = new BlockBuilder()
            .name("trina_lily")
            .material(Material.PLANT)
            .properties(properties -> properties.noCollission().instabreak().sound(SoundType.GRASS))
            .buildBlock(TrinaLilyBlock::new)
            .buildItem(itemBuilder -> itemBuilder.properties(properties -> properties.rarity(Rarity.EPIC)));

    public static final ItemBlock chalkRune = new BlockBuilder()
            .name("chalk_rune")
            .material(Material.DECORATION)
            .properties(properties -> properties.sound(SoundType.STONE).noCollission().noOcclusion().noDrops())
            .renderLayer(() -> RenderType::cutoutMipped)
            .buildBlock(ChalkRuneBlock::new)
            .buildItem(ItemBuilder::noTab);
}
