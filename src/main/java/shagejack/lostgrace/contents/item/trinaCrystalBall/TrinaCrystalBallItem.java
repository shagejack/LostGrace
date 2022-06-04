package shagejack.lostgrace.contents.item.trinaCrystalBall;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import shagejack.lostgrace.registries.effect.AllMobEffects;
import shagejack.lostgrace.registries.item.AllItems;

import java.util.Optional;

public class TrinaCrystalBallItem extends Item {
    public TrinaCrystalBallItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (player == null)
            return InteractionResult.FAIL;

        Optional<LivingEntity> entityOptional = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(2)).stream().findAny();

        if (entityOptional.isPresent()) {
            LivingEntity entity = entityOptional.get();
            if (entity.hasEffect(AllMobEffects.SLEEP)) {
                entity.removeEffect(AllMobEffects.SLEEP);
                entity.stopSleeping();

                if (entity instanceof NeutralMob mob) {
                    mob.setLastHurtByPlayer(player);
                    mob.setPersistentAngerTarget(player.getUUID());
                    mob.startPersistentAngerTimer();
                }

                if (!player.getLevel().isClientSide()) {
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    player.addItem(new ItemStack(AllItems.trinaCrystalBallFull.get()));
                }

                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {
        if (entity.hasEffect(AllMobEffects.SLEEP)) {
            entity.removeEffect(AllMobEffects.SLEEP);
            entity.stopSleeping();
            if (entity instanceof NeutralMob mob) {
                mob.setLastHurtByPlayer(playerIn);
                mob.setPersistentAngerTarget(playerIn.getUUID());
                mob.startPersistentAngerTimer();
            }

            if (!playerIn.getLevel().isClientSide())
                playerIn.addItem(new ItemStack(AllItems.trinaCrystalBallFull.get()));

            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
    }
}
