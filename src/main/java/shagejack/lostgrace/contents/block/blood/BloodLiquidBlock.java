package shagejack.lostgrace.contents.block.blood;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import shagejack.lostgrace.foundation.utility.DropUtils;
import shagejack.lostgrace.registries.item.AllItems;

import java.util.Random;
import java.util.function.Supplier;

public class BloodLiquidBlock extends LiquidBlock {

    public BloodLiquidBlock(Supplier<? extends FlowingFluid> pFluid, Properties pProperties) {
        super(pFluid, pProperties);
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
        super.randomTick(pState, pLevel, pPos, pRandom);

        if (pState.getFluidState().isSource()) {
            pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
            DropUtils.dropItemStack(pLevel, pPos, new ItemStack(AllItems.scab.get(), pRandom.nextInt(2, 7)));
        }
    }


}
