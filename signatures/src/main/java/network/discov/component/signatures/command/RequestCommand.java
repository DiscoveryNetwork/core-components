package network.discov.component.signatures.command;

import network.discov.component.signatures.Signatures;
import network.discov.component.signatures.model.SignatureRequest;
import network.discov.core.spigot.model.EmptyTabArgument;
import network.discov.core.spigot.model.ExecutorSubCommand;
import network.discov.core.spigot.model.StringTabArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RequestCommand extends ExecutorSubCommand {

    public RequestCommand() {
        super("Accept a signature request", "core.command.signatures.accept");
        arguments.put(0, new StringTabArgument("accept/deny", true, new String[]{"accept", "deny"}));
        arguments.put(1, new EmptyTabArgument("uuid", true));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Signatures component = Signatures.getInstance();
        if (!(sender instanceof Player)) {
            sender.sendMessage(component.getGlobalMessage("player-only"));
            return;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(component.getMessage("request-not-found"));
            return;
        }

        SignatureRequest request = component.getRequestManager().getRequest(uuid);
        if (request == null) {
            sender.sendMessage(component.getMessage("request-not-found"));
            return;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            component.getSignatureManager().addSignature(request);
            request.getReceiver().sendMessage(component.getMessage("request-accepted-receiver", request.getSender().getDisplayName()));
            request.getSender().sendMessage(component.getMessage("request-accepted-sender", request.getReceiver().getDisplayName()));
            component.getRequestManager().removeRequest(request);
        } else if (args[0].equalsIgnoreCase("deny")) {
            request.getReceiver().sendMessage(component.getMessage("request-denied-receiver", request.getSender().getDisplayName()));
            request.getSender().sendMessage(component.getMessage("request-denied-sender", request.getReceiver().getDisplayName()));
            component.getRequestManager().removeRequest(request);
        }
    }
}
