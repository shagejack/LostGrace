package shagejack.lostgrace.contents.block.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.contents.item.spellBook.SpellBookItem;
import shagejack.lostgrace.registries.block.AllBlocks;
import shagejack.lostgrace.registries.item.AllItems;
import shagejack.lostgrace.registries.record.ItemBlock;

import java.util.Optional;
import java.util.function.Consumer;

public enum RuneType {
    UNKNOWN(AllBlocks.chalkRune, AllItems.spellBookIntroduction)

    ;

    final ItemBlock rune;
    final RegistryObject<Item> book;

    RuneType(ItemBlock rune, RegistryObject<Item> book) {
        this.rune = rune;
        this.book = book;
    }

    public Block getRuneBlock() {
        if (this.rune != null) {
            return this.rune.block().get();
        } else {
            return Blocks.AIR;
        }
    }

    public SpellBookItem getRuneBook() {
        return (SpellBookItem) this.book.get();
    }

    public static boolean isRune(Level level, BlockPos pos, RuneType type) {
        if (type == null)
            return false;

        return level.getBlockState(pos).is(type.getRuneBlock());
    }

    public static Optional<RuneType> getRuneType(SpellBookItem book) {
        for (RuneType rune : values()) {
            if (rune.getRuneBook().equals(book))
                return Optional.of(rune);
        }

        return Optional.empty();
    }

    public static boolean ifPresent(SpellBookItem book, Consumer<RuneType> runeConsumer) {
        Optional<RuneType> runeType = getRuneType(book);
        if (runeType.isEmpty())
            return false;

        runeConsumer.accept(runeType.get());
        return true;
    }
}
