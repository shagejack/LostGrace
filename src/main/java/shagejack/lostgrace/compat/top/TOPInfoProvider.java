package shagejack.lostgrace.compat.top;

import mcjty.theoneprobe.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.contents.block.bloodAltar.BloodAltarTileEntity;
import shagejack.lostgrace.contents.block.dreamPool.DreamPoolTileEntity;
import shagejack.lostgrace.contents.block.grace.GraceTileEntity;
import shagejack.lostgrace.contents.grace.GraceProvider;
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
        if (blockState.is(AllBlocks.grace.block().get())) {
            TileEntityUtils.get(GraceTileEntity.class, level, pos).ifPresent(te -> {
                player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY).ifPresent(graceHandler -> {
                    if (graceHandler.contains(te.getGrace())) {
                        if (te.getGrace().hasName()) {
                            iProbeInfo.mcText(new TranslatableComponent("lostgrace.top.info.grace_name").append(": " + te.getGraceName()).withStyle(ChatFormatting.GOLD));
                        }
                    }
                });

                if (te.isLocked()) {
                    iProbeInfo.mcText(new TranslatableComponent("lostgrace.top.info.grace_using").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                }
            });
        }

        if (blockState.is(AllBlocks.bloodAltar.block().get())) {
            TileEntityUtils.get(BloodAltarTileEntity.class, level, pos).ifPresent(te -> iProbeInfo.tank(te.bloodTank));
        }

        if (blockState.is(AllBlocks.dreamPool.block().get())) {
            TileEntityUtils.get(DreamPoolTileEntity.class, level, pos).ifPresent(te -> iProbeInfo.tank(te.tank));
        }
    }
}
