package network.discov.component.commons.command;

import network.discov.component.commons.Commons;
import network.discov.core.spigot.model.Command;
import network.discov.core.spigot.model.StringTabArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand extends Command {
    public SpeedCommand() {
        super("speed", "Change your flying/walking speed.", "core.command.speed", new String[]{});
        arguments.put(0, new StringTabArgument("speed", true, new String[]{}));
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Commons.getInstance().getGlobalMessage("player-only"));
            return;
        }

        Player player = (Player) sender;
        float value = Math.max(0, Math.min(10, Float.parseFloat(args[0])));
        if (player.isFlying()) {
            player.setFlySpeed(value / 10);
            sender.sendMessage(Commons.getInstance().getMessage("fly-speed-change", String.valueOf(value)));
        } else {
            player.setWalkSpeed(value / 10);
            sender.sendMessage(Commons.getInstance().getMessage("walk-speed-change", String.valueOf(value)));
        }
    }
}
