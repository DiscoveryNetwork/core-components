package network.discov.component.test;

import network.discov.core.spigot.model.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class TestCommand extends Command {

    public TestCommand() {
        super("test", "Test command for DiscovCore", "test.command", new String[]{});
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        Bukkit.getLogger().info(String.valueOf(Test.getInstance()));
        commandSender.sendMessage("Command success!");
    }
}
