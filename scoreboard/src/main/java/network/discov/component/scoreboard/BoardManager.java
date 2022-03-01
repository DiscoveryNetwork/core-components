package network.discov.component.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BoardManager {
    private final String title;
    private final HashMap<Integer, List<String>> visitorLines;
    private final HashMap<Integer, List<String>> staffLines;
    private final HashMap<UUID, Integer> boardTasks = new HashMap<>();

    public BoardManager(HashMap<Integer, List<String>> visitorLines, HashMap<Integer, List<String>> staffLines) {
        ScoreBoard instance = ScoreBoard.getInstance();
        this.title = instance.getTitle();
        this.visitorLines = visitorLines;
        this.staffLines = staffLines;
    }

    public void init(Player player) {
        PlayerBoard board = new PlayerBoard(player, title);
        ScoreBoard instance = ScoreBoard.getInstance();
        int task = instance.getScheduler().runTaskTimerAsynchronously(board, 0, instance.getInterval()).getTaskId();
        boardTasks.put(player.getUniqueId(), task);
    }

    public void remove(Player player) {
        player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
        boardTasks.remove(player.getUniqueId());
    }

    public HashMap<Integer, List<String>> getLines(Player player) {
        if (player.hasPermission("discovsuite.chat.staff")) {
            return staffLines;
        }
        return visitorLines;
    }
}
