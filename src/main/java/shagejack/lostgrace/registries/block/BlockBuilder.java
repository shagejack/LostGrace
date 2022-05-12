package shagejack.lostgrace.registries.block;

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

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockBuilder {
    protected String name;
    protected RegistryObject<Block> block;
    protected BlockBehaviour.Properties properties;
    protected BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;
    protected String customItemName = "";

    public BlockBuilder() {
        this.defaultProperties();
    }

    public BlockBuilder name(String name) {
        this.name = name;
        return this;
    }

    public BlockBuilder defaultProperties() {
        this.properties = BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 6.0f);
        return this;
    }

    public BlockBuilder properties(Function<BlockBehaviour.Properties, BlockBehaviour.Properties> factory) {
        if (properties == null) {
            this.properties = BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 6.0f);
        }
        this.properties = factory.apply(this.properties);
        return this;
    }

    public BlockBuilder buildBlock(Supplier<Block> blockSupplier) {
        Objects.requireNonNull(name);
        block = RegisterHandle.BLOCK_REGISTER.register(name, blockSupplier);
        LostGrace.LOGGER.debug("register Block:{}", name);
        return this;
    }

    public <T extends Block> BlockBuilder buildBlock(Function<BlockBehaviour.Properties, T> factory) {
        return buildBlock(() -> factory.apply(properties));
    }

    RegistryObject<Block> checkAlreadyBuild() {
        return Objects.requireNonNull(block, "can't build ItemBlock before block is built");
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
    //  BlockBuilder Parameters
    // =============================

    public BlockBuilder blockItemFactory(BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory) {
        this.blockItemFactory = blockItemFactory;
        return this;
    }

    public BlockBuilder customItemName(String itemName) {
        this.customItemName = itemName;
        return this;
    }
}
