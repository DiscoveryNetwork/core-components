package network.discov.component.buildtools.command;

import network.discov.component.buildtools.BuildTools;
import network.discov.component.buildtools.model.ReferencePoint;
import network.discov.core.spigot.model.ExecutorSubCommand;
import network.discov.core.spigot.model.TabArgument;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ListReferenceCommand extends ExecutorSubCommand {

    public ListReferenceCommand() {
        super("List all reference points", "core.command.outlines.list");
        arguments.put(0, new PageTabArgument());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        BuildTools component = BuildTools.getInstance();
        List<ReferencePoint> points = component.getReferenceManager().getPoints();
        double pages = Math.ceil((double) (points.size() / 5) + 1);
        int page = 1;

        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
                if (page > pages) { page = 1; }
            } catch (NumberFormatException e) {
                sender.sendMessage(component.getMessage("invalid-page-number", args[0]));
            }
        }

        sender.sendMessage(component.getMessage("outlines-reference-list-header", String.valueOf(page)));
        for (int i = (page - 1) * 5; i < page * 5 && i < points.size(); i++) {
            Location loc = points.get(i).getLocation();
            sender.sendMessage(component.getMessage("outlines-reference-list-item", points.get(i).getName(), String.valueOf(loc.getBlockX()), String.valueOf(loc.getBlockY()), String.valueOf(loc.getBlockZ())));
        }
        if (page < pages) {
            sender.sendMessage(component.getMessage("outlines-reference-list-footer", String.valueOf(page + 1)));
        }
    }
}

class PageTabArgument extends TabArgument {

    public PageTabArgument() {
        super("page", false);
    }

    @Override
    public List<String> getSuggestions(CommandSender commandSender) {
        List<ReferencePoint> points = BuildTools.getInstance().getReferenceManager().getPoints();
        List<String> suggestions = new ArrayList<>();
        double pages = Math.ceil((double) (points.size() / 5) + 1);
        for (int i = 1; i < pages; i++) {
            suggestions.add(String.valueOf(i));
        }
        return suggestions;
    }
}