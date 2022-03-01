package network.discov.component.buildtools.command;

import network.discov.component.buildtools.BuildTools;
import network.discov.component.buildtools.model.ReferencePointTabArgument;
import network.discov.core.spigot.model.ExecutorSubCommand;
import org.bukkit.command.CommandSender;

public class RemoveReferenceCommand extends ExecutorSubCommand {
    public RemoveReferenceCommand() {
        super("Remove a reference point", "core.command.outlines.remove");
        arguments.put(0, new ReferencePointTabArgument(new String[]{}));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        BuildTools component = BuildTools.getInstance();
        if (component.getReferenceManager().removePoint(args[0])) {
            sender.sendMessage(component.getMessage("outlines-reference-remove", args[0]));
        } else {
            sender.sendMessage(component.getMessage("outlines-reference-not-found", args[0]));
        }
    }
}
