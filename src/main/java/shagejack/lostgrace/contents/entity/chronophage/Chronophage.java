package shagejack.lostgrace.contents.entity.chronophage;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import shagejack.lostgrace.contents.entity.hyperdimensional.Entity4D;
import shagejack.lostgrace.contents.entity.hyperdimensional.MoveControl4D;
import shagejack.lostgrace.foundation.entity.EntityDataSerializersLG;
import shagejack.lostgrace.foundation.utility.*;
import shagejack.lostgrace.foundation.utility.Color;
import shagejack.lostgrace.registries.entity.AllEntityTypes;
import shagejack.lostgrace.registries.item.AllItems;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Random;

public class Chronophage extends PathfinderMob implements Entity4D {
    private static final EntityDataAccessor<Double> DATA_W = SynchedEntityData.defineId(Chronophage.class, EntityDataSerializersLG.DOUBLE);
    private static final EntityDataAccessor<Double> DATA_RADIUS = SynchedEntityData.defineId(Chronophage.class, EntityDataSerializersLG.DOUBLE);
    private static final EntityDataAccessor<Color> DATA_COLOR = SynchedEntityData.defineId(Chronophage.class, EntityDataSerializersLG.COLOR);
    private static final EntityDataAccessor<Integer> DATA_SUMMONED_TICKS = SynchedEntityData.defineId(Chronophage.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANGRY_TICKS = SynchedEntityData.defineId(Chronophage.class, EntityDataSerializers.INT);

    private final ServerBossEvent bossEvent = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);

    public static final double DEFAULT_RADIUS = 5.0D;

    public Chronophage(EntityType<? extends Chronophage> entityType, Level level) {
        super(entityType, level);
        this.setHealth(this.getMaxHealth());
        this.moveControl = new ChronophageMoveControl(this);
        this.xpReward = 50;
    }

    @SuppressWarnings("unchecked")
    public Chronophage(Level level, double posW, double radius) {
        this((EntityType<? extends Chronophage>) AllEntityTypes.chronophage.get(), level);
        this.setW(posW);
        this.setRadius(radius);
    }

    @SuppressWarnings("unchecked")
    public Chronophage(Level level, double posW, double radius, boolean asSummoned) {
        this((EntityType<? extends Chronophage>) AllEntityTypes.chronophage.get(), level);
        this.setW(posW);
        this.setRadius(radius);

        if (asSummoned) {
            this.initSummoned(100);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_W, 0.0D);
        this.getEntityData().define(DATA_RADIUS, DEFAULT_RADIUS);
        this.getEntityData().define(DATA_COLOR, Color.BLACK);
        this.getEntityData().define(DATA_SUMMONED_TICKS, 0);
        this.getEntityData().define(DATA_ANGRY_TICKS, 0);
    }

    public void initSummoned(int ticks) {
        this.getEntityData().set(DATA_SUMMONED_TICKS, ticks);
    }

    public int getSummonedTicks() {
        return this.getEntityData().get(DATA_SUMMONED_TICKS);
    }

    public void reduceSummonedTicks() {
        this.getEntityData().set(DATA_SUMMONED_TICKS, getSummonedTicks() - 1);
    }

    public void makeAngry(int ticks) {
        this.getEntityData().set(DATA_ANGRY_TICKS, ticks);
    }

    public int getAngryTicks() {
        return this.getEntityData().get(DATA_ANGRY_TICKS);
    }

    public void reduceAngryTicks() {
        this.getEntityData().set(DATA_ANGRY_TICKS, getAngryTicks() - 1);
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

        this.setCustomName(new TranslatableComponent("entity.lostgrace.chronophage").withStyle(TextUtils.randomFormat(this.getRandom())));

        if (getSummonedTicks() > 0) {
            if (this.getLevel().noCollision(this.getBoundingBox(getWCloserTo3D(this.getRadius() / 100)))) {
                this.moveTowards3D(this.getRadius() / 100);
            }
            reduceSummonedTicks();
        }

        if (getAngryTicks() > 0) {
            if (getTarget() != null) {
                Vec3 target = getTarget().getEyePosition();
                this.moveControl.setWantedPosition(target.x, target.y, target.z, 1.5D);
            }

            reduceAngryTicks();
        }

        if (Math.abs(getW()) > getRadius() + 10) {
            this.discard();
        }

        this.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(this.blockPosition()).inflate(getRadiusIn3D())).stream().filter(entity -> !(entity instanceof Chronophage) && Vector3.of(entity).distance(Vector3.atCenterOf(this.blockPosition())) <= getRadiusIn3D())
                .forEach(entity -> {
                    entity.hurt(DamageSource.OUT_OF_WORLD, 2.0f + entity.getMaxHealth() * 0.05f);
                    Objects.requireNonNull(entity.getAttribute(Attributes.MAX_HEALTH)).addPermanentModifier(new AttributeModifier("Destined death", -2.0F - entity.getMaxHealth() * 0.05, AttributeModifier.Operation.ADDITION));

                    if (entity.getMaxHealth() <= 2.0f)
                        entity.kill();
                });

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new ChronophageCuriosityGoal());
        this.goalSelector.addGoal(8, new ChronophageRandomMoveGoal());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 10.0D).add(Attributes.MAX_HEALTH, 500.0D).add(Attributes.ATTACK_DAMAGE, 20.0D).add(Attributes.MOVEMENT_SPEED, 3.0D);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setW(tag.getDouble("PosW"));
        if (tag.contains("Radius", Tag.TAG_DOUBLE)) {
            this.setRadius(tag.getDouble("Radius"));
        } else {
            this.setRadius(DEFAULT_RADIUS);
        }
        this.setColor(new Color(tag.getInt("Color")));
        initSummoned(tag.getInt("SummonedTicks"));
        makeAngry(tag.getInt("AngryTicks"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("PosW", getW());
        tag.putDouble("Radius", getRadius());
        tag.putInt("Color", getRawColor().getRGB());
        tag.putInt("SummonedTicks", this.getSummonedTicks());
        tag.putInt("AngryTicks", this.getAngryTicks());
    }

    @Override
    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    protected AABB makeBoundingBox() {
        return getBoundingBox(this.getW());
    }

    @Override
    public void setW(double w) {
        this.getEntityData().set(DATA_W, w);
        double diameter = this.getRadiusIn3D(w) * 2;
        this.setBoundingBox(makeBoundingBox());
        if (diameter > 0) {
            this.setInvulnerable(false);
            this.setInvisible(false);
        } else {
            this.setInvulnerable(true);
            this.setInvisible(true);
        }
    }

    @Override
    public AABB getBoundingBox(double w) {
        double diameter = this.getRadiusIn3D(w) * 2;
        if (diameter > 0) {
            return AABB.ofSize(Vec3.atCenterOf(BlockPos.ZERO), diameter, diameter, diameter).move(this.position());
        } else {
            return EMPTY_BOUNDING.move(this.position());
        }
    }

    @Override
    public double getW() {
        return this.getEntityData().get(DATA_W);
    }

    public double getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    public Color getRawColor() {
        return this.getEntityData().get(DATA_COLOR);
    }

    public Color getColor() {
        Color color = getRawColor();
        int angryTicks = this.getAngryTicks();
        return new Color(Math.min(255, color.getRed() + angryTicks / 5), Math.max(0, color.getGreen() - angryTicks / 5), Math.max(0, color.getBlue() - angryTicks / 5));
    }

    public void setColor(Color color) {
        this.getEntityData().set(DATA_COLOR, color);
    }

    public void setRadius(double radius) {
        this.getEntityData().set(DATA_RADIUS, radius);
    }

    public double getRadiusIn3D() {
        return getRadiusIn3D(this.getW());
    }

    public double getRadiusIn3D(double w) {
        double radius = getRadius();
        if (getW() < radius) {
            return Math.sqrt(radius * radius - w * w);
        }
        return 0.0D;
    }

    @Override
    public double distanceToSqr(Entity pEntity) {
        return super.distanceToSqr(pEntity) + getW() * getW();
    }

    @Override
    public float distanceTo(Entity pEntity) {
        return (float) Math.sqrt(distanceToSqr(pEntity));
    }

    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        this.bossEvent.addPlayer(pPlayer);
    }

    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossEvent.removePlayer(pPlayer);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.heal(tryEatTime());
    }

    protected float tryEatTime() {
        final double speed;

        if (this.getHealth() < this.getMaxHealth()) {
            if (this.getHealth() < this.getMaxHealth() / 2) {
                speed = 4.0;
            } else {
                speed = 2.0;
            }
        } else if (this.random.nextDouble() < 0.1) {
            speed = 1.0;
        } else {
            return 0.0f;
        }

        AtomicDouble heal = new AtomicDouble();
        LevelUtils.inRadius(this.getLevel(), this.blockPosition(), this.getRadiusIn3D() + 3.0 + getAngryTicks() / 400.0, (level, pos) -> {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (state.isAir())
                return;

            if (level instanceof ServerLevel serverLevel && block.isRandomlyTicking(state)) {
                heal.getAndAdd(0.01);
                for (int i = 0; i < level.getRandom().nextInt(1, 3) * speed; i++) {
                    state.randomTick(serverLevel, pos, level.getRandom());
                }
            }

            if (block instanceof EntityBlock entityBlock) {
                TileEntityUtils.get(BlockEntity.class, level, pos).ifPresent(te -> {
                    if (te.isRemoved())
                        return;

                    BlockEntityTicker<BlockEntity> ticker = (BlockEntityTicker<BlockEntity>) entityBlock.getTicker(level, state, te.getType());

                    if (ticker == null)
                        return;

                    heal.getAndAdd(0.01);

                    for (int i = 0; i < level.getRandom().nextInt(150, 300) * speed; i++) {
                        if (te.isRemoved())
                            return;
                        ticker.tick(level, pos, state, te);
                    }
                });
            }
        });

        level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(3.0 + getAngryTicks() / 400.0), entity -> !(entity instanceof Chronophage)).forEach(entity -> {
            // sanity check
            if (entity.is(this) || entity instanceof Chronophage)
                return;

            heal.getAndAdd(0.01);
            for (int i = 0; i < level.getRandom().nextInt(150, 300) * speed; i++) {
                entity.tick();
            }
        });

        return heal.floatValue();
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (super.hurt(pSource, pAmount)) {
            this.makeAngry(1200);
            if (pSource.getEntity() instanceof LivingEntity living) {
                this.setTarget(living);
            }
        }
        return false;
    }

    @Override
    public void push(Entity pEntity) {

    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {

    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return super.isInvulnerableTo(pSource) || pSource == DamageSource.OUT_OF_WORLD || pSource == DamageSource.IN_WALL || pSource.isExplosion();
    }

    @Override
    public boolean isAttackable() {
        return getRadiusIn3D() > 0;
    }

    @Override
    public void kill() {
        super.discard();
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        ItemEntity itementity = this.spawnAtLocation(AllItems.blackKnife.get());
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }
    }

    class ChronophageCuriosityGoal extends Goal {
        public ChronophageCuriosityGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            if (Chronophage.this.getTarget() != null && !Chronophage.this.getMoveControl().hasWanted() && Chronophage.this.random.nextInt(reducedTickDelay(7)) == 0) {
                return Chronophage.this.distanceToSqr(Chronophage.this.getTarget()) > 4.0D;
            } else {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return Chronophage.this.getMoveControl().hasWanted() && Chronophage.this.getTarget() != null && Chronophage.this.getTarget().isAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            LivingEntity livingentity = Chronophage.this.getTarget();
            if (livingentity != null) {
                Vec3 vec3 = livingentity.getEyePosition();
                Chronophage.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 0.5D);
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
            LivingEntity livingentity = Chronophage.this.getTarget();
            if (livingentity != null) {
                Vec3 vec3 = livingentity.getEyePosition();
                Chronophage.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
            }
        }
    }

    class ChronophageMoveControl extends MoveControl4D {
        public ChronophageMoveControl(Chronophage entity) {
            super(entity);
        }

        @Override
        public void tick() {
            super.tick();
        }
    }

    class ChronophageRandomMoveGoal extends Goal {
        public ChronophageRandomMoveGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return !Chronophage.this.getMoveControl().hasWanted() && Chronophage.this.random.nextInt(reducedTickDelay(7)) == 0;
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
            Random random = new Random(System.currentTimeMillis() / 10000);

            Vector4 offset = new Vector4(random.nextDouble(15) - 7, random.nextDouble(11) - 5, random.nextDouble(15) - 7, random.nextDouble(5.0D) - 10.0D);
            Vector4 target = Vector4.of(Vector3.of(Chronophage.this), getW()).add(offset);

            if (random.nextDouble() < 0.5) {
                if (Chronophage.this.level.noCollision(getBoundingBox().move(offset.get3DPart().toVec3()))) {
                    Chronophage.this.moveControl.setWantedPosition(target.x(), target.y(), target.z(), 0.5D);
                }
            } else {
                // move in 4D
                if (Chronophage.this.level.noCollision(getBoundingBox(target.w()).move(offset.get3DPart().toVec3()))) {
                    ((MoveControl4D) Chronophage.this.moveControl).setWantedPosition(target.x(), target.y(), target.z(), target.w(), 0.5D);
                }
            }
        }
    }

}
