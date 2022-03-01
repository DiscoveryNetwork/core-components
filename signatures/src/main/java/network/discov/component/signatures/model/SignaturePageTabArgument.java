package network.discov.component.signatures.model;

import network.discov.component.signatures.Signatures;
import network.discov.core.spigot.model.TabArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SignaturePageTabArgument extends TabArgument {
    public SignaturePageTabArgument() {
        super("pageNumber", true);
    }

    @Override
    public List<String> getSuggestions(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;
        List<String> suggestions = new ArrayList<>();
        for (int page : Signatures.getInstance().getSignatureManager().getValidPageNumbers(player)) {
            suggestions.add(String.valueOf(page));
        }
        return suggestions;
    }
}
