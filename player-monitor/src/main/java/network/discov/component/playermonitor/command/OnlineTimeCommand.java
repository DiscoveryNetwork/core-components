package network.discov.component.playermonitor.command;

import net.md_5.bungee.api.CommandSender;
import network.discov.core.bungee.model.Command;

public class OnlineTimeCommand extends Command {
    public OnlineTimeCommand() {
        super("onlinetime", "View your total online time", "core.command.onlinetime", new String[]{});
    }

    @Override
    public void executeCommand(CommandSender commandSender, String[] strings) {

    }
}
