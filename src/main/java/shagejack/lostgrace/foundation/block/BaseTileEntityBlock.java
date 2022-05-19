package shagejack.lostgrace.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import shagejack.lostgrace.foundation.tile.BaseTileEntity;

public abstract class BaseTileEntityBlock<T extends BaseTileEntity> extends Block implements ITE<T> {

    public BaseTileEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (oldState.hasBlockEntity() && oldState.getBlock() != newState.getBlock()) {
            withTileEntityDo(level, pos, T::onRemoved);
            level.removeBlockEntity(pos);
        }
    }


}
