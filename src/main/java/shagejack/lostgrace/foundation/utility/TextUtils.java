package shagejack.lostgrace.foundation.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TextUtils {

    private static final Random RANDOM = new Random();
    public static final List<ChatFormatting> FORMATTING = Arrays.asList(ChatFormatting.values());

    private TextUtils() {
        throw new IllegalStateException(this.getClass().toString() + "should not be instantiated as it's a utility class.");
    }

    public static ChatFormatting randomFormat() {
        return randomFormat(RANDOM);
    }

    public static ChatFormatting randomFormat(Random random) {
        return FORMATTING.get(random.nextInt(0, FORMATTING.size()));
    }

    public static Component corrupt(String text) {
        return corrupt(text, RANDOM);
    }

    public static Component corrupt(String text, Random random) {
        TextComponent component = new TextComponent("");
        for (char ch : text.toCharArray()) {
            double rand = random.nextDouble();
            if (rand < 0.4) {
                component.append(new TextComponent(String.valueOf(ch)).withStyle(randomFormat(random)).withStyle(randomFormat(random)));
            } else if (rand < 0.8) {
                component.append(new TextComponent("x").withStyle(ChatFormatting.OBFUSCATED));
            }
        }
        return component;
    }

    // TODO: corrupt translatable component
    public static Component corrupt(TranslatableComponent text, Random random) {
        return text;
    }

}
