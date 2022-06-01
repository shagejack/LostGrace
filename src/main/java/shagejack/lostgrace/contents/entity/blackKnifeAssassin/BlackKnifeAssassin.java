package shagejack.lostgrace.contents.entity.blackKnifeAssassin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import shagejack.lostgrace.contents.entity.hyperdimensional.Entity4D;
import shagejack.lostgrace.contents.entity.hyperdimensional.MoveControl4D;
import shagejack.lostgrace.foundation.utility.Vector3;
import shagejack.lostgrace.registries.entity.AllEntityTypes;

import java.awt.*;
import java.util.EnumSet;
import java.util.Random;

public class BlackKnifeAssassin extends Monster implements Entity4D {

    public static final double DEFAULT_RADIUS = 5.0D;

    private double posW;
    private double radius;
    private Color color;

    public BlackKnifeAssassin(EntityType<? extends BlackKnifeAssassin> entityType, Level level) {
        super(entityType, level);
        this.setHealth(this.getMaxHealth());
        this.moveControl = new BlackKnifeAssassinMoveControl(this);
        this.xpReward = 50;
        this.posW = 0.0D;
        this.radius = DEFAULT_RADIUS;
        this.color = Color.GRAY;
    }

    public BlackKnifeAssassin(Level level, double posW, double radius) {
        this((EntityType<? extends BlackKnifeAssassin>) AllEntityTypes.blackKnifeAssassin.get(), level);
        this.posW = posW;
        this.radius = radius;
    }

    public void move(MoverType pType, Vec3 pPos) {
        super.move(pType, pPos);
        this.checkInsideBlocks();
    }

    public void move4D(MoverType pType, Vec3 pPos, double movement4) {
        super.move(pType, pPos);
        this.setW(getW() + movement4);
        this.checkInsideBlocks();
    }

    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        this.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(this.blockPosition()).inflate(getRadiusIn3D())).stream().filter(entity -> Vector3.of(entity).distance(Vector3.atCenterOf(this.blockPosition())) <= getRadiusIn3D()).forEach(LivingEntity::kill);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new BlackKnifeAssassinCuriosityGoal());
        this.goalSelector.addGoal(8, new BlackKnifeAssassinRandomMoveGoal());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 500.0D).add(Attributes.ATTACK_DAMAGE, 20.0D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setW(tag.getDouble("PosW"));
        if (tag.contains("Radius", Tag.TAG_DOUBLE)) {
            this.setRadius(tag.getDouble("Radius"));
        } else {
            this.setRadius(DEFAULT_RADIUS);
        }
        this.setColor(new Color(tag.getInt("Color")));
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("PosW", getW());
        tag.putDouble("Radius", getRadius());
        tag.putInt("Color", getColor().getRGB());
    }

    @Override
    public void setW(double w) {
        this.posW = w;
        double diameter = this.getRadiusIn3D(w) * 2;
        if (diameter > 0) {
            this.setInvulnerable(false);
            this.setInvisible(false);
            this.setBoundingBox(AABB.ofSize(Vec3.atCenterOf(BlockPos.ZERO), diameter, diameter, diameter));
        } else {
            this.setInvulnerable(true);
            this.setInvisible(true);
            this.setBoundingBox(EMPTY_BOUNDING);
        }
    }

    @Override
    public AABB getBoundingBox(double w) {
        double diameter = this.getRadiusIn3D(w) * 2;
        if (diameter > 0) {
            return AABB.ofSize(Vec3.atCenterOf(BlockPos.ZERO), diameter, diameter, diameter);
        } else {
            return EMPTY_BOUNDING;
        }
    }

    @Override
    public double getW() {
        return this.posW;
    }

    public double getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadiusIn3D() {
        return getRadiusIn3D(this.posW);
    }

    public double getRadiusIn3D(double w) {
        if (posW < radius) {
            return Math.sqrt(radius * radius - w * w);
        }
        return 0.0D;
    }

    @Override
    public double distanceToSqr(Entity pEntity) {
        return this.distanceToSqr(pEntity) + getW() * getW();
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!this.isInvulnerableTo(pSource)) {
            this.markHurt();
        }
        return false;
    }

    class BlackKnifeAssassinCuriosityGoal extends Goal {
        public BlackKnifeAssassinCuriosityGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            if (BlackKnifeAssassin.this.getTarget() != null && !BlackKnifeAssassin.this.getMoveControl().hasWanted() && BlackKnifeAssassin.this.random.nextInt(reducedTickDelay(7)) == 0) {
                return BlackKnifeAssassin.this.distanceToSqr(BlackKnifeAssassin.this.getTarget()) > 4.0D;
            } else {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return BlackKnifeAssassin.this.getMoveControl().hasWanted() && BlackKnifeAssassin.this.getTarget() != null && BlackKnifeAssassin.this.getTarget().isAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            LivingEntity livingentity = BlackKnifeAssassin.this.getTarget();
            if (livingentity != null) {
                Vec3 vec3 = livingentity.getEyePosition();
                BlackKnifeAssassin.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
            }
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            LivingEntity livingentity = BlackKnifeAssassin.this.getTarget();
            if (livingentity != null) {
                Vec3 vec3 = livingentity.getEyePosition();
                BlackKnifeAssassin.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
            }
        }
    }

    class BlackKnifeAssassinMoveControl extends MoveControl4D {
        public BlackKnifeAssassinMoveControl(BlackKnifeAssassin entity) {
            super(entity);
        }

        @Override
        public void tick() {
            super.tick();
        }
    }

    class BlackKnifeAssassinRandomMoveGoal extends Goal {
        public BlackKnifeAssassinRandomMoveGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return !BlackKnifeAssassin.this.getMoveControl().hasWanted() && BlackKnifeAssassin.this.random.nextInt(reducedTickDelay(7)) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return false;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            Random random = new Random(System.currentTimeMillis() / 20000);

            BlockPos blockpos = BlackKnifeAssassin.this.blockPosition().offset(random.nextInt(15) - 7, random.nextInt(11) - 5, random.nextInt(15) - 7);

            if (random.nextDouble() < 0.5) {
                if (BlackKnifeAssassin.this.level.isEmptyBlock(blockpos)) {
                    BlackKnifeAssassin.this.moveControl.setWantedPosition((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D, 0.25D);
                }
            } else if (random.nextDouble() < 0.5) {
                // move in 4D
                ((MoveControl4D) BlackKnifeAssassin.this.moveControl).setWantedPosition((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D, getW() + random.nextDouble(5.0D) - 10.0D, 0.25D);
            } else {
                // move to 3D
                BlackKnifeAssassin.this.moveControl.setWantedPosition((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D, 0.25D);
            }
        }
    }

}
