package network.discov.component.buildtools.command;

import network.discov.component.buildtools.BuildTools;
import network.discov.core.spigot.model.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand extends Command {

    public BuildCommand() {
        super("build", "Toggle BuildMode", "core.command.build", new String[]{});
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        BuildTools component = BuildTools.getInstance();
        if (!(sender instanceof Player)) {
            sender.sendMessage(component.getGlobalMessage("player-only"));
            return;
        }

        Player player = (Player) sender;
        if (component.getBuildModeManager().hasPlayer(player)) {
            component.getBuildModeManager().disable(player);
            player.sendMessage(component.getMessage("buildmode-disabled"));
        } else {
            component.getBuildModeManager().enable(player);
            player.sendMessage(component.getMessage("buildmode-enabled"));
        }
    }
}
