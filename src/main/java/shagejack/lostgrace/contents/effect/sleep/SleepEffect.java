package shagejack.lostgrace.contents.effect.sleep;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.awt.*;

public class SleepEffect extends MobEffect {

    public SleepEffect() {
        super(MobEffectCategory.HARMFUL, Color.LIGHT_GRAY.getRGB());
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "62D96224-CD0D-4889-B6C7-1200AA12B5E9", -1.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, "9F8137A1-E577-4B0D-BC8A-40453A15B394", -1.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "59ED1A7C-0584-494D-B82F-FE251AE06C6C", -1.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int pAmplifier) {
        if (livingEntity.getLevel().isNight()) {
            livingEntity.startSleeping(livingEntity.blockPosition());
        } else {
            if (livingEntity.isPassenger()) {
                livingEntity.stopRiding();
            }

            livingEntity.setPose(Pose.SLEEPING);
            livingEntity.setDeltaMovement(Vec3.ZERO);
            livingEntity.hasImpulse = true;
        }

        Vec3 pos = livingEntity.getBoundingBox().getCenter();
        if (livingEntity.getLevel() instanceof ServerLevel level) {
            level.getPlayers(p -> p.distanceTo(livingEntity) <= 512).forEach(p -> level.sendParticles(p, ParticleTypes.CLOUD, true, pos.x(), pos.y(), pos.z(), 100, 0.3D, 0.3D, 0.3D, 0.02D));
        }
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {

    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);

        if (livingEntity.getPose() == Pose.SLEEPING)
            livingEntity.setPose(Pose.STANDING);

    }

    @Override
    public boolean isInstantenous() {
        return false;
    }
}
