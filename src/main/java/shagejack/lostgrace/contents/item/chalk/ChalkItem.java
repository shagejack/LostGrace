package shagejack.lostgrace.contents.item.chalk;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import shagejack.lostgrace.contents.block.spell.RuneType;
import shagejack.lostgrace.contents.item.spellBook.SpellBookItem;

public class ChalkItem extends Item {
    public ChalkItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        if (player == null)
            return InteractionResult.FAIL;


        ItemStack chalk = context.getItemInHand();
        ItemStack book = player.getItemInHand(context.getHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

        if (chalk.isEmpty())
            return InteractionResult.FAIL;

        if (!state.isAir())
            return InteractionResult.FAIL;

        if (!book.isEmpty() && book.getItem() instanceof SpellBookItem spellBookItem) {
            if (RuneType.ifPresent(spellBookItem, rune -> {
                if (rune.getRuneBlock().canSurvive(state, level, pos)) {
                    chalk.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                    level.setBlockAndUpdate(pos, rune.getRuneBlock().defaultBlockState());
                }})
            ) {
                return InteractionResult.PASS;
            }
        } else if (level.isClientSide()) {
            player.sendMessage(new TranslatableComponent("lostgrace.info.chalk_no_book"), Util.NIL_UUID);
        }

        return InteractionResult.FAIL;
    }
}
