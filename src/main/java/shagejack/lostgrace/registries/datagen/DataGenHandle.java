package shagejack.lostgrace.registries.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.foundation.utility.Wrapper;

import java.util.ArrayList;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = LostGrace.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenHandle {

    private static final ArrayList<TagTask<Block>> blockTagsTasks = new ArrayList<>();
    private static final ArrayList<TagTask<Item>> itemTagsTasks = new ArrayList<>();
    private static final ArrayList<TagTask<? extends Fluid>> fluidTagsTasks = new ArrayList<>();
    private static final Wrapper<BlockTagsProvider> blockTagsPro = new Wrapper<>();
    private static final Wrapper<ItemTagsProvider> itemTagsPro = new Wrapper<>();
    private static final Wrapper<FluidTagsProvider> fluidTagsPro = new Wrapper<>();

    public static void runOnDataGen(Supplier<DistExecutor.SafeRunnable> toRun) {
        if (FMLEnvironment.dist == Dist.CLIENT && LostGrace.isDataGen) {
            toRun.get().run();
        }
    }

    public static void addBlockTagTask(TagTask<Block> task) {
        runOnDataGen(() -> () -> blockTagsTasks.add(task));
    }
    public static void addItemTagTask(TagTask<Item> task) {
        runOnDataGen(() -> () -> itemTagsTasks.add(task));
    }
    public static void addFluidTagTask(TagTask<? extends Fluid> task) {
        runOnDataGen(() -> () -> fluidTagsTasks.add(task));
    }

    @SubscribeEvent
    public static void processDataGen(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        BlockTagsProvider blockTagsProvider = new BlockTagsProvider(generator, LostGrace.MOD_ID, existingFileHelper) {
            @Override
            protected void addTags() {
                blockTagsTasks.forEach(task -> task.run((block, tag) -> tag(BlockTags.create(new ResourceLocation(tag))).add(block)));
            }
        };

        ItemTagsProvider itemTagsProvider = new ItemTagsProvider(generator, blockTagsProvider, LostGrace.MOD_ID, existingFileHelper) {
            @Override
            protected void addTags() {
                itemTagsTasks.forEach(task -> task.run((item, tag) -> tag(ItemTags.create(new ResourceLocation(tag))).add(item)));
            }
        };

        FluidTagsProvider fluidTagsProvider = new FluidTagsProvider(generator, LostGrace.MOD_ID, existingFileHelper) {
            @Override
            protected void addTags() {
                fluidTagsTasks.forEach(task -> task.run((fluid, tag) -> tag(FluidTags.create(new ResourceLocation(tag))).add(fluid)));
            }
        };

        blockTagsPro.set(() -> blockTagsProvider);
        itemTagsPro.set(() -> itemTagsProvider);
        fluidTagsPro.set(() -> fluidTagsProvider);

        generator.addProvider(blockTagsProvider);
        generator.addProvider(itemTagsProvider);
        generator.addProvider(fluidTagsProvider);
    }
}
