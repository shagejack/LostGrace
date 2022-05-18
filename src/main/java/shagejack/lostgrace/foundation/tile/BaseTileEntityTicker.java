package shagejack.lostgrace.foundation.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public class BaseTileEntityTicker<T extends BlockEntity> implements BlockEntityTicker<T> {

    @Override
    public void tick(Level level, BlockPos pos, BlockState state, T te) {
        if (!te.hasLevel())
            te.setLevel(level);
        ((BaseTileEntity) te).tick();
    }
}
