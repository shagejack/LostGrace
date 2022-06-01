package shagejack.lostgrace.contents.item.chalk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import shagejack.lostgrace.contents.block.spell.SpellType;
import shagejack.lostgrace.contents.item.spellBook.SpellBookItem;
import shagejack.lostgrace.registries.item.AllItems;

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

        if (!chalk.isEmpty() && !book.isEmpty() && book.getItem() instanceof SpellBookItem spellBookItem && state.isAir()) {
            SpellType type = getSpellType(spellBookItem);
            if (type != SpellType.NULL && type.getSpellBlock().canSurvive(state, level, pos)) {
                chalk.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                level.setBlockAndUpdate(pos, type.getSpellBlock().defaultBlockState());
                return InteractionResult.PASS;
            }
        }

        return InteractionResult.FAIL;
    }

    public SpellType getSpellType(SpellBookItem book) {
        if (book == AllItems.spellBookIntroduction.get())
            return SpellType.UNKNOWN;

        return SpellType.NULL;
    }
}