package network.discov.component.playermonitor.command;

import net.md_5.bungee.api.CommandSender;
import network.discov.core.bungee.model.Command;

public class CheckTimeCommand extends Command {
    public CheckTimeCommand() {
        super("checktime", "Check if you've met your weekly hours", "core.command.checktime", new String[]{});
        arguments.put(0, new PlayerTabArgument(false));
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {

    }
}
