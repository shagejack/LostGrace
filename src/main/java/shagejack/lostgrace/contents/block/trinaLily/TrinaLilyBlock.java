package shagejack.lostgrace.contents.block.trinaLily;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import shagejack.lostgrace.registries.effect.AllMobEffects;

import java.util.Random;

public class TrinaLilyBlock extends FlowerBlock {
    public TrinaLilyBlock(Properties properties) {
        super(AllMobEffects.SLEEP, 16, properties);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRandom) {
        VoxelShape voxelshape = this.getShape(pState, pLevel, pPos, CollisionContext.empty());
        Vec3 vec3 = voxelshape.bounds().getCenter();
        double d0 = (double)pPos.getX() + vec3.x;
        double d1 = (double)pPos.getZ() + vec3.z;

        for(int i = 0; i < 3; ++i) {
            if (pRandom.nextBoolean()) {
                pLevel.addParticle(ParticleTypes.END_ROD, d0 + (pRandom.nextBoolean() ? pRandom.nextDouble() / 5.0D : - pRandom.nextDouble() / 5.0D), (double)pPos.getY() + 0.5D + pRandom.nextDouble(0.5D), d1 + (pRandom.nextBoolean() ? pRandom.nextDouble() / 5.0D : - pRandom.nextDouble() / 5.0D), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (!pLevel.isClientSide) {
            if (pEntity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 2));
            }
        }
    }


}
