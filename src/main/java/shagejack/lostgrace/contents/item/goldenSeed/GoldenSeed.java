package shagejack.lostgrace.contents.item.goldenSeed;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import shagejack.lostgrace.registries.block.AllBlocks;

public class GoldenSeed extends Item {
    public GoldenSeed(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        BlockPos inPos = entity.getOnPos().above();
        Level level = entity.getLevel();

        if (level.getBlockState(inPos).is(Blocks.FIRE)) {
            level.setBlock(inPos, AllBlocks.grace.block().get().defaultBlockState(), 3);
            entity.remove(Entity.RemovalReason.KILLED);
            return true;
        }
        return false;
    }
}
