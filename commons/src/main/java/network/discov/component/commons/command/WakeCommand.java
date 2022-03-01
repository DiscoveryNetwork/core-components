package network.discov.component.commons.command;

import network.discov.component.commons.Commons;
import network.discov.core.spigot.Core;
import network.discov.core.spigot.model.Command;
import network.discov.core.spigot.model.PlayerTabArgument;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WakeCommand extends Command {
    public WakeCommand() {
        super("wake", "Try and wake someone by displaying audio and visual signs.", "core.command.wake", new String[]{});
        arguments.put(0, new PlayerTabArgument(true));
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Commons.getInstance().getGlobalMessage("player-not-found"));
            return;
        }

        int task = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F), 0, 5).getTaskId();
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> Bukkit.getScheduler().cancelTask(task), 30);
        target.sendMessage("§a§lWAKEY, WAKEY!");
    }
}
