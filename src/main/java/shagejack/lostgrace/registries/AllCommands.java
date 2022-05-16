package shagejack.lostgrace.registries;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.command.ConfigCommand;
import shagejack.lostgrace.commands.CommandVisitAllGraces;

public class AllCommands {

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event)
    {
        new CommandVisitAllGraces(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

}
