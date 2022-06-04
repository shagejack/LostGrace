package shagejack.lostgrace.registries.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.registries.AllTabs;
import shagejack.lostgrace.registries.RegisterHandle;
import shagejack.lostgrace.registries.tags.TagBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ItemBuilder {
    protected String name;
    protected CreativeModeTab tab = AllTabs.tabMain;
    protected boolean hasTab = true;
    protected Item.Properties properties;
    protected UnaryOperator<Item.Properties> operator;
    protected final List<String> tags = new ArrayList<>();
    protected RegistryObject<Item> registryObject;

    public ItemBuilder() {
        this.defaultProperties();
    }

    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder defaultProperties() {
        this.properties = new Item.Properties();
        return this;
    }

    public ItemBuilder properties(UnaryOperator<Item.Properties> operator) {
        if (this.operator == null) {
            this.operator = operator;
        } else {
            this.operator = (UnaryOperator<Item.Properties>) this.operator.andThen(operator);
        }
        return this;
    }

    public ItemBuilder tab(CreativeModeTab tab) {
        this.hasTab = true;
        this.tab = tab;
        return this;
    }

    public ItemBuilder noTab() {
        this.hasTab = false;
        return this;
    }

    public void checkProperties() {
        if (properties == null) {
            defaultProperties();
        }

        if (operator != null) {
            this.properties = operator.apply(properties);
        }

        if (hasTab) {
            this.properties.tab(tab);
        }
    }

    public RegistryObject<Item> build() {
        return build(() -> new Item(properties));
    }

    public RegistryObject<Item> build(RegistryObject<Block> block) {
        return build(block, BlockItem::new);
    }

    public <T extends Item> RegistryObject<Item> build(Function<Item.Properties, T> factory) {
        return build(() -> factory.apply(properties));
    }

    public <T extends BlockItem> RegistryObject<Item> build(RegistryObject<Block> block, BiFunction<Block, Item.Properties, T> blockItemFactory) {
        return build(() -> blockItemFactory.apply(block.get(), properties));
    }

    public RegistryObject<Item> build(Supplier<Item> itemSupplier) {
        checkProperties();
        registryObject = RegisterHandle.ITEM_REGISTER.register(name, itemSupplier);

        if (!tags.isEmpty()) {
            TagBuilder.itemTag(registryObject, tags);
            LostGrace.LOGGER.debug("Register Item: {} with Tags: {}", name, tags.toString());
        } else {
            LostGrace.LOGGER.debug("Register Item: {}", name);
        }

        return registryObject;
    }

    public ItemBuilder tags(TagKey<?>... tags) {
        this.tags.addAll(Arrays.stream(tags).map(tag -> tag.location().toString()).toList());
        return this;
    }

    public ItemBuilder tags(ResourceLocation... tags) {
        this.tags.addAll(Arrays.stream(tags).map(ResourceLocation::toString).toList());
        return this;
    }

    public ItemBuilder tags(String... tags) {
        this.tags.addAll(List.of(tags));
        return this;
    }

}
