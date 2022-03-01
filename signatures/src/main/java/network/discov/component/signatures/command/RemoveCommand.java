package network.discov.component.signatures.command;

import network.discov.core.spigot.model.ExecutorSubCommand;
import network.discov.component.signatures.Signatures;
import network.discov.component.signatures.model.SignaturePageTabArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoveCommand extends ExecutorSubCommand {
    public RemoveCommand() {
        super("Remove one of your signatures", "core.command.signatures.remove");
        arguments.put(0, new SignaturePageTabArgument());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Signatures component = Signatures.getInstance();
        if (!(sender instanceof Player)) {
            sender.sendMessage(component.getGlobalMessage("player-only"));
            return;
        }

        Player player = (Player) sender;
        List<Integer> validPages = component.getSignatureManager().getValidPageNumbers(player);
        int pageNumber = 0;
        try {
            pageNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(component.getGlobalMessage("error-parse-number", args[0]));
            return;
        }

        if (!validPages.contains(pageNumber)) {
            sender.sendMessage(component.getMessage("invalid-page-number"));
            return;
        }

        component.getSignatureManager().removeSignature(player, pageNumber);
        sender.sendMessage(component.getMessage("signature-removed", String.valueOf(pageNumber)));
    }
}
