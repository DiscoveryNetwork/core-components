package network.discov.component.commons.command;

import network.discov.component.commons.Commons;
import network.discov.core.spigot.model.Command;
import network.discov.core.spigot.model.PlayerTabArgument;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends Command {
    public FlyCommand() {
        super("fly", "Toggle your flight mode.", "core.command.fly", new String[]{});
        arguments.put(0, new PlayerTabArgument(false));
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        Player target = null;
        if (args.length == 1) {
            if (!sender.hasPermission("core.command.fly.others")) {
                sender.sendMessage(Commons.getInstance().getGlobalMessage("no-permission"));
                return;
            }
            target = Bukkit.getPlayer(args[0]);
        }

        if (target == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Commons.getInstance().getGlobalMessage("player-only"));
                return;
            }

            target = (Player) sender;
        }

        target.setAllowFlight(!target.getAllowFlight());
        if (target.getAllowFlight()) {
            sender.sendMessage(Commons.getInstance().getMessage("flight-enabled"));
        } else {
            target.setFlying(false);
            sender.sendMessage(Commons.getInstance().getMessage("flight-disabled"));
        }
    }
}
