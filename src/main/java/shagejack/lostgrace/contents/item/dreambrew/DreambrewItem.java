package shagejack.lostgrace.contents.item.dreambrew;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class DreambrewItem extends Item {

    public DreambrewItem(Properties properties) {
        super(properties);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {

            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            }

            // apply some effects

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);

                if (stack.isEmpty())
                    return new ItemStack(Items.GLASS_BOTTLE);

                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        } else {
            return new ItemStack(Items.GLASS_BOTTLE);
        }

        level.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
        return stack;
    }
}
