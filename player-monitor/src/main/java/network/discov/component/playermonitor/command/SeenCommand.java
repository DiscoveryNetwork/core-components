package network.discov.component.playermonitor.command;

import net.md_5.bungee.api.CommandSender;
import network.discov.core.bungee.model.Command;

public class SeenCommand extends Command {
    public SeenCommand() {
        super("seen", "Check when a player was last online", "core.command.seen", new String[]{});
    }

    @Override
    public void executeCommand(CommandSender commandSender, String[] strings) {

    }
}
