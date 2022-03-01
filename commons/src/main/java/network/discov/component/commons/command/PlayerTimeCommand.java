package network.discov.component.commons.command;

import network.discov.component.commons.Commons;
import network.discov.core.spigot.model.Command;
import network.discov.core.spigot.model.TabArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerTimeCommand extends Command {
    public PlayerTimeCommand() {
        super("playertime", "Change your personal time", "core.command.playertime", new String[]{"ptime"});
        arguments.put(0, new TimeTabArgument());
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Commons.getInstance().getGlobalMessage("player-only"));
            return;
        }

        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("reset")) {
            player.resetPlayerTime();
            sender.sendMessage(Commons.getInstance().getMessage("player-time-reset"));
            return;
        }

        try {
            player.setPlayerTime(getTimeTicks(args[0]), false);
            sender.sendMessage(Commons.getInstance().getMessage("player-time-set", getTimeTicks(args[0]).toString()));
        } catch (NumberFormatException e) {
            sender.sendMessage(Commons.getInstance().getMessage("player-time-invalid"));
        }
    }

    private Long getTimeTicks(String argument) throws NumberFormatException {
        try {
            return Long.parseLong(argument);
        } catch (NumberFormatException ignored) {}

        try {
            return TimeValue.valueOf(argument.toUpperCase()).getValue();
        } catch (IllegalArgumentException ignored) {}

        throw new NumberFormatException();
    }
}

class TimeTabArgument extends TabArgument {

    public TimeTabArgument() {
        super("time", true);
    }

    @Override
    public List<String> getSuggestions(CommandSender commandSender) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("reset");
        for (TimeValue value : TimeValue.values()) {
            suggestions.add(value.toString().toLowerCase());
        }
        return suggestions;
    }
}

enum TimeValue {
    DAY(6000L), NIGHT(18000L), SUNRISE(47500L), SUNSET(12750L);

    private final Long value;

    public Long getValue() {
        return this.value;
    }

    TimeValue(Long value) {
        this.value = value;
    }
}