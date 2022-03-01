package network.discov.component.commons.command;

import network.discov.component.commons.Commons;
import network.discov.core.spigot.model.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearInventoryCommand extends Command {

    public ClearInventoryCommand() {
        super("clearinventory", "Clear your inventory.", "core.commmand.clearinventory", new String[]{"ci"});
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Commons.getInstance().getGlobalMessage("player-only"));
            return;
        }

        Player player = (Player) sender;
        player.getInventory().clear();
        sender.sendMessage(Commons.getInstance().getMessage("inventory-cleared"));
    }
}
