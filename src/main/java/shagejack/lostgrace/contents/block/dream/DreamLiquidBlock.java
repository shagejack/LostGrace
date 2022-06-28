package shagejack.lostgrace.contents.block.dream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import shagejack.lostgrace.foundation.utility.Vector3;

import java.util.Random;
import java.util.function.Supplier;

public class DreamLiquidBlock extends LiquidBlock {
    public DreamLiquidBlock(Supplier<? extends FlowingFluid> fluidSupplier, Properties properties) {
        super(fluidSupplier, properties);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRandom) {
        double height = pState.getFluidState().getOwnHeight();
        for(int i = 0; i < 3; ++i) {
            if (pRandom.nextBoolean()) {
                Vector3.randomNormal(pRandom).multiply(0.01).apply((x, y, z) -> pLevel.addParticle(ParticleTypes.END_ROD, pPos.getX() + pRandom.nextDouble(), pPos.getY() + pRandom.nextDouble(height), pPos.getZ() + pRandom.nextDouble(), x, y, z));
            }
        }

    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (!pLevel.isClientSide) {
            if (pEntity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            }

            Vector3 dest = Vector3.of(pEntity).add(Vector3.randomNormal(pLevel.getRandom()).multiply(Math.pow(pLevel.getRandom().nextDouble(10), 2)));
            pEntity.teleportTo(dest.x(), dest.y(), dest.z());
        }
    }
}
