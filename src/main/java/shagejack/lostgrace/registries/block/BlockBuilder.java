package shagejack.lostgrace.registries.block;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.registries.RegisterHandle;
import shagejack.lostgrace.registries.item.ItemBuilder;
import shagejack.lostgrace.registries.record.ItemBlock;
import shagejack.lostgrace.registries.tags.TagBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class BlockBuilder {
    protected String name;
    protected RegistryObject<Block> registryObject;
    protected BlockBehaviour.Properties properties;
    protected UnaryOperator<BlockBehaviour.Properties> operator;
    protected final List<String> tags = new ArrayList<>();
    protected BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;
    protected String customItemName = "";

    public static final List<Supplier<Runnable>> setupRenderLayerTasks = new ArrayList<>();
    public static final List<BlockColorBinder> blockColorTasks = new ArrayList<>();

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
        checkProperties();
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

    public void checkProperties() {
        if (properties == null) {
            defaultProperties();
        }

        if (operator != null) {
            this.properties = operator.apply(properties);
        }
    }

    public BlockBuilder defaultProperties() {
        this.properties = BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 6.0f);
        return this;
    }

    public BlockBuilder properties(UnaryOperator<BlockBehaviour.Properties> operator) {
        if (this.operator == null) {
            this.operator = operator;
        } else {
            this.operator = (UnaryOperator<BlockBehaviour.Properties>) this.operator.andThen(operator);
        }
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

    public BlockBuilder renderLayer(Supplier<Supplier<RenderType>> renderType) {
        setupRenderLayerTasks.add(() -> () -> ItemBlockRenderTypes.setRenderLayer(registryObject.get(), renderType.get().get()));
        return this;
    }

    public BlockBuilder customItemName(String itemName) {
        this.customItemName = itemName;
        return this;
    }

    public BlockBuilder setBlockColor(Color color) {
        return setBlockColor((state, blockAndTintGetter, pos, pTintIndex) -> color.getRGB());
    }

    public BlockBuilder setBlockColor(BlockColor blockColor) {
        blockColorTasks.add(new BlockColorBinder(() -> registryObject, blockColor));
        return this;
    }

    public static void registerColors(final ColorHandlerEvent.Block event) {
        blockColorTasks.forEach(task -> task.register(event));
    }

    private record BlockColorBinder(Supplier<RegistryObject<Block>> blockSupplier, BlockColor blockColor) {
        private void register(final ColorHandlerEvent.Block event) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    event.getBlockColors().register(blockColor, blockSupplier.get().get())
            );
        }
    }

}
