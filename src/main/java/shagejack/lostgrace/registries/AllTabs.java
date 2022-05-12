package shagejack.lostgrace.registries;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.registries.item.AllItems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class AllTabs {
    public static final List<CreativeModeTab> tabs = new ArrayList<>();

    public static final CreativeModeTab tabMain = tab(LostGrace.MOD_ID, () -> () -> AllItems.memoryOfGrace);

    static private CreativeModeTab tab(String name, Supplier<Supplier<RegistryObject<Item>>> itemStack) {
        var tab = new CreativeModeTab(name) {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(itemStack.get().get().get());
            }
        };
        tabs.add(tab);
        return tab;
    }
}
