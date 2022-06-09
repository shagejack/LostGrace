package shagejack.lostgrace.contents.item.memoryOfGrace;

import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import shagejack.lostgrace.contents.grace.GlobalGraceSet;
import shagejack.lostgrace.contents.grace.Grace;
import shagejack.lostgrace.contents.grace.GraceProvider;
import shagejack.lostgrace.contents.grace.IGraceHandler;
import shagejack.lostgrace.foundation.config.LostGraceConfig;
import shagejack.lostgrace.foundation.utility.Vector3;

public class MemoryOfGraceItem extends Item {
    public MemoryOfGraceItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            if (!level.isClientSide()) {
                player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
                player.clearFire();

                if (LostGraceConfig.MEMORY_OF_GRACE_DROP_EXPERIENCE.get() && !player.isCreative() && !player.isSpectator() && !level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                    int reward = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(player, null, getExperience(player));
                    player.experienceLevel = 0;
                    player.experienceProgress = 0;
                    player.totalExperience = 0;
                    player.connection.send(new ClientboundSetExperiencePacket(0, 0, 0));
                    ExperienceOrb.award((ServerLevel) level, player.position(), reward);
                }

                LazyOptional<IGraceHandler> handler = player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY);

                handler.ifPresent(graceData -> {
                    Grace grace = graceData.getLastGrace();
                    if (grace != Grace.NULL && GlobalGraceSet.contains(grace)) {
                        Level graceLevel = grace.getLevel();
                        Vector3 pos = Vector3.atCenterOf(grace.getPos());
                        if (graceLevel instanceof ServerLevel targetLevel) {
                            player.teleportTo(targetLevel, pos.x(), pos.y(), pos.z(), Mth.wrapDegrees(player.getYRot()), Mth.wrapDegrees(player.getXRot()));
                            graceData.visitGrace(grace, false);
                        }
                    } else {
                        player.stopUsingItem();
                    }
                });
            }
        }
        return stack;
    }

    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    protected int getExperience(Player player) {
        int i = player.experienceLevel * 7;
        return Math.min(i, 100);
    }
}
