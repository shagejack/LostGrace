package shagejack.lostgrace.contents.item.trinaCrystalBall;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import shagejack.lostgrace.registries.item.AllItems;

public class TrinaCrystalBallFullItem extends Item {
    public TrinaCrystalBallFullItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack)
    {
        return new ItemStack(AllItems.trinaCrystalBall.get());
    }
}
