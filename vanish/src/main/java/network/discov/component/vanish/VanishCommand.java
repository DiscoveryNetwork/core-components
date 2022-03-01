package network.discov.component.vanish;

import network.discov.core.spigot.model.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends Command {

    public VanishCommand() {
        super("vanish", "Hide yourself from other players", "core.command.vanish", new String[]{"v"});
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        Vanish vanish = Vanish.getInstance();

        if (!(sender instanceof Player)) {
            sender.sendMessage(vanish.getGlobalMessage("player-only"));
            return;
        }

        Player player = (Player) sender;
        if (vanish.isPlayerHidden(player)) {
            vanish.showPlayer(player, true, true);
            player.sendMessage(vanish.getMessage("player-unvanish"));
        } else {
            vanish.hidePlayer(player, true);
            player.sendMessage(vanish.getMessage("player-vanish"));
        }
    }
}
