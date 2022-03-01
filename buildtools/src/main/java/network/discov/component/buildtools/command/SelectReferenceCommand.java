package network.discov.component.buildtools.command;

import network.discov.component.buildtools.BuildTools;
import network.discov.component.buildtools.model.ReferencePoint;
import network.discov.component.buildtools.model.ReferencePointTabArgument;
import network.discov.core.spigot.model.ExecutorSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectReferenceCommand extends ExecutorSubCommand {
    public SelectReferenceCommand() {
        super("Select a reference point", "core.command.outlines.select");
        arguments.put(0, new ReferencePointTabArgument(new String[]{"none"}));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        BuildTools component = BuildTools.getInstance();
        if (!(sender instanceof Player)) {
            sender.sendMessage(component.getGlobalMessage("player-only"));
            return;
        }

        Player player = (Player) sender;
        ReferencePoint point = component.getReferenceManager().getPoint(args[0]);
        if (point != null) {
            component.getSelectionManager().select(player, point);
            sender.sendMessage(component.getMessage("outlines-reference-selected", point.getName()));
        } else if (args[0].equalsIgnoreCase("none")) {
            component.getSelectionManager().clearSelection(player);
            sender.sendMessage(component.getMessage("outlines-reference-selection-cleared"));
        } else {
            sender.sendMessage(component.getMessage("outlines-reference-not-found", args[0]));
        }
    }
}