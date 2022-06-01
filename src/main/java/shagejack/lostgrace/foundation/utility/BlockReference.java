package shagejack.lostgrace.foundation.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record BlockReference(BlockPos offset, Block block) {
    public static BlockReference of(int x, int y, int z, Block block) {
        return new BlockReference(new BlockPos(x, y, z), block);
    }

    public boolean check(Level level, BlockPos pos) {
        if (block == Blocks.AIR)
            return level.getBlockState(pos).isAir();

        return level.getBlockState(pos.offset(offset)).is(block);
    }
}
