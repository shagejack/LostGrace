package shagejack.lostgrace.registries.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.registries.AllTabs;
import shagejack.lostgrace.registries.RegisterHandle;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemBuilder {
    protected String name;
    protected CreativeModeTab tab = AllTabs.tabMain;
    protected boolean hasTab = true;
    protected Item.Properties properties;
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

    public ItemBuilder properties(Function<Item.Properties, Item.Properties> function) {
        if (properties == null) {
            this.properties = new Item.Properties();
        }
        properties = function.apply(this.properties);
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

    public RegistryObject<Item> build(Supplier<Item> itemSupplier) {
        checkProperties();
        registryObject = RegisterHandle.ITEM_REGISTER.register(name, itemSupplier);
        LostGrace.LOGGER.debug("register Item {}", name);
        return registryObject;
    }

    public <T extends BlockItem> RegistryObject<Item> build(RegistryObject<Block> block, BiFunction<Block, Item.Properties, T> blockItemFactory) {
        checkProperties();
        registryObject = RegisterHandle.ITEM_REGISTER.register(name, () -> blockItemFactory.apply(block.get(), properties) );
        return registryObject;
    }

}
