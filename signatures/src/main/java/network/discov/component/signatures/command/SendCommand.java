package network.discov.component.signatures.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import network.discov.component.signatures.Signatures;
import network.discov.component.signatures.model.SignatureRequest;
import network.discov.core.spigot.model.EmptyTabArgument;
import network.discov.core.spigot.model.ExecutorSubCommand;
import network.discov.core.spigot.model.PlayerTabArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SendCommand extends ExecutorSubCommand {

    public SendCommand() {
        super("Send your signature to someone", "core.command.signatures.send");
        arguments.put(0, new PlayerTabArgument(true));
        arguments.put(1, new EmptyTabArgument("signature", true));
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        Signatures component = Signatures.getInstance();
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(component.getGlobalMessage("player-only"));
            return;
        }

        Player sender = (Player) commandSender;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(component.getGlobalMessage("player-not-found", args[0]));
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String signature = ChatColor.translateAlternateColorCodes('&', message);
        signature += "\n\n§r§8§o- " + sender.getDisplayName();
        SignatureRequest request = new SignatureRequest(sender, target, signature);
        component.getRequestManager().addRequest(request);
        sendRequest(component, request);
        sender.sendMessage(component.getMessage("signature-sent", target.getDisplayName()));
    }

    private void sendRequest(Signatures component, SignatureRequest request) {
        TextComponent main = new TextComponent(component.getMessage("signature-request", request.getSender().getDisplayName()));

        TextComponent accept = new TextComponent(component.getMessage("signature-accept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aAccept request").create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/signatures request accept %s", request.getUniqueId())));

        TextComponent deny = new TextComponent(" " + component.getMessage("signature-deny"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cDeny request").create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/signatures request deny %s", request.getUniqueId())));
        accept.addExtra(deny);

        request.getReceiver().spigot().sendMessage(main);
        request.getReceiver().spigot().sendMessage(accept);
    }
}
