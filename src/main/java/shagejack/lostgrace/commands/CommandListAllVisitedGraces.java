package shagejack.lostgrace.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import shagejack.lostgrace.contents.grace.Grace;
import shagejack.lostgrace.contents.grace.GraceProvider;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandListAllVisitedGraces {

    public CommandListAllVisitedGraces(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("listallvisitedgraces").requires(cs -> cs.hasPermission(2)).executes(this::listAllVisitedGraces));
    }

    private int listAllVisitedGraces(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        String name;

        AtomicInteger count = new AtomicInteger();

        player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY).ifPresent(graceHandler -> {
            for (Grace grace : graceHandler.getAllGracesFound()) {
                context.getSource().sendSuccess(new TextComponent("[" + count + "]" + grace.toString()), true);
                count.getAndIncrement();
            }
        });

        context.getSource().sendSuccess(new TextComponent(count + " visited graces found."), true);

        return 1;
    }

}