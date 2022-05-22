package shagejack.lostgrace.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Function;

public class AllTags {



    public static <T> TagKey<T> tag(Function<ResourceLocation, TagKey<T>> wrapperFactory, String namespace,
                                    String path) {
        return wrapperFactory.apply(new ResourceLocation(namespace, path));
    }

    public static <T> TagKey<T> forgeTag(Function<ResourceLocation, TagKey<T>> wrapperFactory, String path) {
        return tag(wrapperFactory, "forge", path);
    }

    public static TagKey<Block> forgeBlockTag(String path) {
        return forgeTag(BlockTags::create, path);
    }

    public static TagKey<Item> forgeItemTag(String path) {
        return forgeTag(ItemTags::create, path);
    }

    public static TagKey<Fluid> forgeFluidTag(String path) {
        return forgeTag(FluidTags::create, path);
    }

    public static <T> TagKey<T> modTag(Function<ResourceLocation, TagKey<T>> wrapperFactory, String path) {
        return wrapperFactory.apply(new ResourceLocation(path));
    }

    public static TagKey<Block> modBlockTag(String path) {
        return modTag(BlockTags::create, path);
    }

    public static TagKey<Item> modItemTag(String path) {
        return modTag(ItemTags::create, path);
    }

    public static TagKey<Fluid> modFluidTag(String path) {
        return modTag(FluidTags::create, path);
    }
}
