package shagejack.lostgrace.contents.item.memoryOfGrace;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import shagejack.lostgrace.contents.grace.GlobalGraceSet;
import shagejack.lostgrace.contents.grace.Grace;
import shagejack.lostgrace.contents.grace.GraceProvider;
import shagejack.lostgrace.contents.grace.IGraceHandler;

import java.util.concurrent.atomic.AtomicReference;

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
        if (entity instanceof Player player) {
            if (!level.isClientSide()) {
                LazyOptional<IGraceHandler> handler = player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY);

                handler.ifPresent(graceData -> {
                    Grace grace = graceData.getLastGrace();
                    if (grace != Grace.NULL && GlobalGraceSet.contains(grace)) {
                        Level targetLevel = grace.getLevel();
                        Vec3 pos = Vec3.atCenterOf(grace.getPos());
                        if (targetLevel != null) {
                            if (!targetLevel.dimension().location().equals(player.getLevel().dimension().location())) {
                                player.changeDimension((ServerLevel) targetLevel);
                            }
                            player.teleportTo(pos.x(), pos.y(), pos.z());
                        }
                    } else {
                        player.stopUsingItem();
                    }
                });
            }

            if (player.isCreative()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }
}
