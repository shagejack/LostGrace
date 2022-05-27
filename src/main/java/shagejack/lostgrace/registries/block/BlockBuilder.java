package shagejack.lostgrace.registries.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.registries.RegisterHandle;
import shagejack.lostgrace.registries.item.ItemBuilder;
import shagejack.lostgrace.registries.record.ItemBlock;
import shagejack.lostgrace.registries.tags.TagBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockBuilder {
    protected String name;
    protected RegistryObject<Block> registryObject;
    protected BlockBehaviour.Properties properties;
    protected final List<String> tags = new ArrayList<>();
    protected BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;
    protected String customItemName = "";

    public BlockBuilder() {
        this.defaultProperties();
    }

    public BlockBuilder name(String name) {
        this.name = name;
        return this;
    }

    public BlockBuilder buildBlock(Supplier<Block> blockSupplier) {
        Objects.requireNonNull(name);
        registryObject = RegisterHandle.BLOCK_REGISTER.register(name, blockSupplier);
        if (!tags.isEmpty()) {
            TagBuilder.blockTag(registryObject, tags);
            LostGrace.LOGGER.debug("Register Block: {} with Tags: {}", name, tags.toString());
        } else {
            LostGrace.LOGGER.debug("Register Block: {}", name);
        }
        return this;
    }

    public <T extends Block> BlockBuilder buildBlock(Function<BlockBehaviour.Properties, T> factory) {
        return buildBlock(() -> factory.apply(properties));
    }

    RegistryObject<Block> checkAlreadyBuild() {
        return Objects.requireNonNull(registryObject, "can't build ItemBlock before block is built");
    }

    // =============================
    //  Build Item Method Overloads
    // =============================

    public ItemBlock buildItem(Function<ItemBuilder, ItemBuilder> factory) {
        final var itemName = this.customItemName.isEmpty() ? this.name : this.customItemName;
        final var block = checkAlreadyBuild();
        final ItemBuilder itemBuilder = new ItemBuilder().name(itemName);
        factory.apply(itemBuilder);
        LostGrace.LOGGER.debug("register Block:{} with Item:{}", name, itemName);
        ItemBlock itemBlock = this.blockItemFactory == null ? new ItemBlock(itemBuilder.build(block), block) : new ItemBlock(itemBuilder.build(block, this.blockItemFactory), block);
        return itemBlock;
    }

    public ItemBlock buildItem() {
        return buildItem(itemBuilder -> itemBuilder);
    }

    // =============================
    //  No Item
    // =============================

    public RegistryObject<Block> noItem() {
        return checkAlreadyBuild();
    }

    // =============================
    //  BlockBuilder Parameters
    // =============================

    public BlockBuilder defaultProperties() {
        this.properties = BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 6.0f);
        return this;
    }

    public BlockBuilder properties(Function<BlockBehaviour.Properties, BlockBehaviour.Properties> factory) {
        if (properties == null) {
            defaultProperties();
        }
        this.properties = factory.apply(this.properties);
        return this;
    }

    public BlockBuilder material(Material material) {
        this.properties = BlockBehaviour.Properties.of(material);
        return this;
    }

    public BlockBuilder tags(TagKey<?>... tags) {
        this.tags.addAll(Arrays.stream(tags).map(tag -> tag.location().toString()).toList());
        return this;
    }

    public BlockBuilder tags(ResourceLocation... tags) {
        this.tags.addAll(Arrays.stream(tags).map(ResourceLocation::toString).toList());
        return this;
    }

    public BlockBuilder tags(String... tags) {
        this.tags.addAll(List.of(tags));
        return this;
    }

    public BlockBuilder blockItemFactory(BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory) {
        this.blockItemFactory = blockItemFactory;
        return this;
    }

    public BlockBuilder customItemName(String itemName) {
        this.customItemName = itemName;
        return this;
    }

}
