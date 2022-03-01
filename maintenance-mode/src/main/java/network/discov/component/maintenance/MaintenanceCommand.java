package network.discov.component.maintenance;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import network.discov.core.bungee.model.Command;
import network.discov.core.bungee.model.StringTabArgument;

public class MaintenanceCommand extends Command {
    protected MaintenanceCommand() {
        super("maintenance", "Toggle maintenance mode", "core.command.maintenance", new String[]{});
        arguments.put(0, new StringTabArgument("enable/disable", true, new String[]{"enable", "disable"}));
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        MaintenanceMode component = MaintenanceMode.getInstance();
        if (args[0].equalsIgnoreCase("enable")) {
            component.enable();
            sender.sendMessage(new TextComponent(component.getMessage("enabled")));
        } else if (args[0].equalsIgnoreCase("disable")) {
            component.disable();
            sender.sendMessage(new TextComponent(component.getMessage("disabled")));
        }
    }
}
