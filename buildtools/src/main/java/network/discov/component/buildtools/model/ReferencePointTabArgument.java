package network.discov.component.buildtools.model;

import network.discov.component.buildtools.BuildTools;
import network.discov.core.spigot.model.TabArgument;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReferencePointTabArgument extends TabArgument {
    String[] baseSuggestions;

    public ReferencePointTabArgument(String[] baseSuggestions) {
        super("name", true);
        this.baseSuggestions = baseSuggestions;
    }

    @Override
    public List<String> getSuggestions(CommandSender commandSender) {
        List<String> suggestions = new ArrayList<>(Arrays.asList(baseSuggestions));
        for (ReferencePoint point : BuildTools.getInstance().getReferenceManager().getPoints()) {
            suggestions.add(point.getName());
        }
        return suggestions;
    }
}