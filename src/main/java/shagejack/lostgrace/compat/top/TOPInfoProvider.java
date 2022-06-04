package shagejack.lostgrace.compat.top;

import mcjty.theoneprobe.api.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.contents.block.bloodAltar.BloodAltarTileEntity;
import shagejack.lostgrace.contents.block.dreamPool.DreamPoolTileEntity;
import shagejack.lostgrace.foundation.utility.TileEntityUtils;
import shagejack.lostgrace.registries.block.AllBlocks;

public class TOPInfoProvider implements IProbeInfoProvider {
    @Override
    public ResourceLocation getID() {
        return LostGrace.asResource("lostgracedata");
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        if (level == null || blockState == null || iProbeHitData == null || player == null)
            return;

        BlockPos pos = iProbeHitData.getPos();
        if (blockState.is(AllBlocks.bloodAltar.block().get())) {
            TileEntityUtils.get(BloodAltarTileEntity.class, level, pos).ifPresent(te -> iProbeInfo.tank(te.bloodTank));
        }

        if (blockState.is(AllBlocks.dreamPool.block().get())) {
            TileEntityUtils.get(DreamPoolTileEntity.class, level, pos).ifPresent(te -> iProbeInfo.tank(te.tank));
        }
    }
}
