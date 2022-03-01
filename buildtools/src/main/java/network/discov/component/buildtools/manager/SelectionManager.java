package network.discov.component.buildtools.manager;

import network.discov.component.buildtools.BuildTools;
import network.discov.component.buildtools.model.ReferenceParticle;
import network.discov.component.buildtools.model.ReferencePoint;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class SelectionManager {
    private final HashMap<UUID, ReferencePoint> selections = new HashMap<>();
    private final HashMap<UUID, Integer> tasks = new HashMap<>();

    public boolean hasSelection(Player player) {
        return selections.containsKey(player.getUniqueId());
    }

    public void select(Player player, ReferencePoint point) {
        if (tasks.get(player.getUniqueId()) != null) {
            Bukkit.getScheduler().cancelTask(tasks.get(player.getUniqueId()));
        }
        selections.put(player.getUniqueId(), point);
        int taskId = BuildTools.getInstance().getScheduler().runTaskTimerAsynchronously(new ReferenceParticle(player, point), 0, 40).getTaskId();
        tasks.put(player.getUniqueId(), taskId);
    }

    public void clearSelection(Player player) {
        if (tasks.get(player.getUniqueId()) != null) {
            Bukkit.getScheduler().cancelTask(tasks.get(player.getUniqueId()));
        }
        tasks.remove(player.getUniqueId());
        selections.remove(player.getUniqueId());
    }

    public ReferencePoint getSelection(Player player) {
        return selections.get(player.getUniqueId());
    }
}
