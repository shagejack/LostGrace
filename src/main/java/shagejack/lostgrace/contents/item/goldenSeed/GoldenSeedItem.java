package shagejack.lostgrace.contents.item.goldenSeed;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import shagejack.lostgrace.contents.block.grace.GraceTileEntityLateInitializer;
import shagejack.lostgrace.foundation.tile.TileEntityLateInitializationHandler;
import shagejack.lostgrace.foundation.utility.Vector3;
import shagejack.lostgrace.registries.block.AllBlocks;

public class GoldenSeedItem extends Item {
    public GoldenSeedItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        BlockPos inPos = entity.getOnPos().above();
        Level level = entity.getLevel();

        ItemStack seedStack = entity.getItem();

        if (level.getBlockState(inPos).is(Blocks.FIRE)) {
            level.setBlock(inPos, AllBlocks.grace.block().get().defaultBlockState(), 3);
            for (int i = 0; i < 500; i++) {
                Vector3.randomNormal(level.getRandom()).multiply(0.3).apply((x, y, z) -> level.addParticle(ParticleTypes.FLAME, inPos.getX() + 0.5D, inPos.getY() + 0.5D, inPos.getZ() + 0.5D, x, y, z));
            }
            if (seedStack.hasCustomHoverName()) {
                TileEntityLateInitializationHandler.getInstance().offer(new GraceTileEntityLateInitializer(level, inPos, te -> te.setGraceName(entity.getItem().getHoverName().getContents())));
            }
            entity.discard();
            return true;
        }
        return false;
    }
}
