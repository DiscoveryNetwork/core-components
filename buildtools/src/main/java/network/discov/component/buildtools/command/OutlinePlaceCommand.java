package network.discov.component.buildtools.command;

import network.discov.component.buildtools.BuildTools;
import network.discov.core.spigot.model.EmptyTabArgument;
import network.discov.core.spigot.model.ExecutorSubCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OutlinePlaceCommand extends ExecutorSubCommand {

    public OutlinePlaceCommand() {
        super("Place an outline block", "core.command.outlines.place");
        arguments.put(0, new EmptyTabArgument("length", true));
        arguments.put(1, new EmptyTabArgument("heading", true));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        BuildTools component = BuildTools.getInstance();
        double length;
        double heading;

        if (!(sender instanceof Player)) {
            sender.sendMessage(component.getGlobalMessage("player-only"));
            return;
        }

        Player player = (Player) sender;
        if (!component.getSelectionManager().hasSelection(player)) {
            sender.sendMessage(component.getMessage("outlines-no-reference-selected"));
            return;
        }

        try {
            length = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(component.getGlobalMessage("error-parse-number", args[0]));
            return;
        }

        try {
            heading = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(component.getGlobalMessage("error-parse-number", args[1]));
            return;
        }

        Location pointLocation = component.getSelectionManager().getSelection(player).getLocation();
        double radHeading = heading / 180.0D * 3.141592653589793D;
        double preRoundX = length * Math.sin(radHeading);
        double preRoundZ = length * Math.cos(radHeading);
        double postRoundX = (double) Math.round(preRoundX);
        double postRoundZ = (double) Math.round(preRoundZ);
        int changeX = (int) postRoundX;
        int changeZ = (int) postRoundZ;

        Location block = new Location(pointLocation.getWorld(), (pointLocation.getX() + changeX), player.getLocation().getY(), (pointLocation.getZ() - changeZ));
        block.getBlock().setType(Material.GOLD_BLOCK);
        sender.sendMessage(component.getMessage("outlines-block-place", String.valueOf(block.getBlockX()), String.valueOf(block.getBlockY()), String.valueOf(block.getBlockZ())));
    }
}
