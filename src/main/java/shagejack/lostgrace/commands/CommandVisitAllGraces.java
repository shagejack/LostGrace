package shagejack.lostgrace.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import shagejack.lostgrace.contents.grace.GlobalGraceSet;
import shagejack.lostgrace.contents.grace.Grace;
import shagejack.lostgrace.contents.grace.GraceProvider;

import java.util.Set;
import java.util.stream.Collectors;

public class CommandVisitAllGraces {

    public CommandVisitAllGraces(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("visitallgraces").requires(cs -> cs.hasPermission(2)).executes(cs -> visitAllGraces(cs, false))
                .then(Commands.argument("gracename", StringArgumentType.string()).executes(cs -> visitAllGraces(cs, true)))
        );
    }

    private int visitAllGraces(CommandContext<CommandSourceStack> context, boolean requireName) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        String name;

        Set<Grace> graceSet;

        if (requireName) {
            name = StringArgumentType.getString(context, "gracename");
            graceSet = GlobalGraceSet.getGraceSet().stream().filter(grace -> grace.hasName() && grace.getName().equals(name)).collect(Collectors.toSet());
        } else {
            graceSet = GlobalGraceSet.getGraceSet();
        }

        for (Grace grace : graceSet) {
            player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY).ifPresent(graceHandler -> graceHandler.visitGrace(grace));
            context.getSource().sendSuccess(new TextComponent(grace.toString() + " visited."), true);
        }

        return 1;
    }

}