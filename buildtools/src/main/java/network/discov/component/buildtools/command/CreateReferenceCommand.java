package network.discov.component.buildtools.command;

import network.discov.component.buildtools.BuildTools;
import network.discov.core.spigot.model.EmptyTabArgument;
import network.discov.core.spigot.model.ExecutorSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateReferenceCommand extends ExecutorSubCommand {
    public CreateReferenceCommand() {
        super("Create a reference point", "core.command.outlines.create");
        arguments.put(0, new EmptyTabArgument("name", true));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        BuildTools component = BuildTools.getInstance();
        if (!(sender instanceof Player)) {
            sender.sendMessage(component.getGlobalMessage("player-only"));
            return;
        }

        Player player = (Player) sender;
        component.getReferenceManager().addPoint(args[0], player.getLocation());
        player.sendMessage(component.getMessage("outlines-reference-create", args[0]));
    }
}
