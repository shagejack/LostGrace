package shagejack.lostgrace.registries.block;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.contents.block.blood.BloodLiquidBlock;
import shagejack.lostgrace.contents.block.bloodAltar.BloodAltarBlock;
import shagejack.lostgrace.contents.block.fresh.FreshBlock;
import shagejack.lostgrace.contents.block.fresh.RottenFreshBlock;
import shagejack.lostgrace.contents.block.grace.GraceBlock;
import shagejack.lostgrace.registries.fluid.AllFluids;
import shagejack.lostgrace.registries.item.ItemBuilder;
import shagejack.lostgrace.registries.record.ItemBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AllBlocks {

    public static final ItemBlock grace = new BlockBuilder()
            .name("grace")
            .properties(properties -> properties.lightLevel(state -> 15).noCollission())
            .buildBlock(GraceBlock::new)
            .buildItem(ItemBuilder::noTab);

    public static final ItemBlock bloodAltar = new BlockBuilder()
            .name("blood_altar")
            .properties(properties -> properties)
            .buildBlock(BloodAltarBlock::new)
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
}
