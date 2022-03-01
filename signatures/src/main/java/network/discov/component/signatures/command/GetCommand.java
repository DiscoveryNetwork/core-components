package network.discov.component.signatures.command;

import network.discov.core.spigot.model.ExecutorSubCommand;
import network.discov.component.signatures.Signatures;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetCommand extends ExecutorSubCommand {
    public GetCommand() {
        super("Get your autograph book!", "core.command.signatures.get");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Signatures.getInstance().getGlobalMessage("player-only"));
            return;
        }

        Player player = (Player) sender;
        Signatures.getInstance().getSignatureManager().giveItem(player);
    }
}
