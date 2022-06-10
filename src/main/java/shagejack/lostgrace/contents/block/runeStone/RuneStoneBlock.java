package shagejack.lostgrace.contents.block.runeStone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.compress.utils.Lists;
import shagejack.lostgrace.contents.block.grace.GraceTileEntity;
import shagejack.lostgrace.foundation.utility.TileEntityUtils;

import java.util.List;

public class RuneStoneBlock extends Block {

    public RuneStoneBlock(Properties properties) {
        super(properties);
    }

    @Deprecated
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        getNearbyGraceTileEntities(level, pos).forEach(GraceTileEntity::tryInitTableGrace);
    }

    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        getNearbyGraceTileEntities(level, pos).forEach(te -> te.setTable(false));
    }

    public static List<GraceTileEntity> getNearbyGraceTileEntities(Level level, BlockPos pos) {
        List<GraceTileEntity> tiles = Lists.newArrayList();
        TileEntityUtils.get(GraceTileEntity.class, level, pos.above()).ifPresent(tiles::add);
        TileEntityUtils.get(GraceTileEntity.class, level, pos.offset(1, 0, 0)).ifPresent(tiles::add);
        TileEntityUtils.get(GraceTileEntity.class, level, pos.offset(-1, 0, 0)).ifPresent(tiles::add);
        TileEntityUtils.get(GraceTileEntity.class, level, pos.offset(0, 0, 1)).ifPresent(tiles::add);
        TileEntityUtils.get(GraceTileEntity.class, level, pos.offset(0, 0, -1)).ifPresent(tiles::add);
        TileEntityUtils.get(GraceTileEntity.class, level, pos.offset(1, 0, 1)).ifPresent(tiles::add);
        TileEntityUtils.get(GraceTileEntity.class, level, pos.offset(1, 0, -1)).ifPresent(tiles::add);
        TileEntityUtils.get(GraceTileEntity.class, level, pos.offset(-1, 0, 1)).ifPresent(tiles::add);
        TileEntityUtils.get(GraceTileEntity.class, level, pos.offset(-1, 0, -1)).ifPresent(tiles::add);
        return tiles;
    }

}
