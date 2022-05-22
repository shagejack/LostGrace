package shagejack.lostgrace.contents.block.bloodAltar;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import shagejack.lostgrace.foundation.block.BaseTileEntityBlock;
import shagejack.lostgrace.registries.tile.AllTileEntities;

public class BloodAltarBlock extends BaseTileEntityBlock<BloodAltarTileEntity> {
    public BloodAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (player.getItemInHand(hand).isEmpty()) {
            withTileEntityDo(level, pos, BloodAltarTileEntity::tryStart);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public Class<BloodAltarTileEntity> getTileEntityClass() {
        return BloodAltarTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends BloodAltarTileEntity> getTileEntityType() {
        return (BlockEntityType<? extends BloodAltarTileEntity>) AllTileEntities.bloodAltar.get();
    }
}
