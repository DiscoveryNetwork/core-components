package network.discov.component.scoreboard;

import me.clip.placeholderapi.PlaceholderAPI;

import network.discov.core.spigot.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PlayerBoard implements Runnable {
    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final HashMap<Integer, String> stringCache = new HashMap<>();
    private final HashMap<Integer, Integer> indexCache = new HashMap<>();
    private final int size;

    public PlayerBoard(Player player, String boardTitle) {
        this.player = player;
        this.size = ScoreBoard.getInstance().getMaxLength();
        scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        objective = scoreboard.registerNewObjective("dsb1", "dsb2", "dsb3");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        String title = PlaceholderAPI.setPlaceholders(player, boardTitle);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));

        HashMap<Integer, List<String>> lines = getLines();
        for (Integer key : lines.keySet()) {
            List<String> values = lines.get(key);
            if (values.size() == 1) {
                stringCache.put(key, prep(values.get(0)));
                objective.getScore(prep(values.get(0))).setScore(lines.size() - key);
            } else if (values.size() > 1) {
                stringCache.put(key, trim(prep(values.get(0))));
                indexCache.put(key, 0);
                objective.getScore(trim(prep(values.get(0)))).setScore(lines.size() - key);
            }

        }
        player.setScoreboard(scoreboard);
    }

    public void update() {
        HashMap<Integer, List<String>> lines = getLines();
        for (Integer key : lines.keySet()) {
            List<String> values = lines.get(key);
            if (values.size() == 1) {
                if (!objective.getScore(prep(values.get(0))).isScoreSet()) {
                    scoreboard.resetScores(stringCache.get(key));
                    objective.getScore(prep(values.get(0))).setScore(lines.size() - key);
                    stringCache.put(key, prep(values.get(0)));
                }
            } else if (values.size() > 1) {
                scoreboard.resetScores(stringCache.get(key));
                int index = indexCache.get(key);
                if (index < (values.size() - 1)) {
                    objective.getScore(trim(prep(values.get(index + 1)))).setScore(lines.size() - key);
                    indexCache.put(key, index + 1);
                    stringCache.put(key, trim(prep(values.get(index + 1))));
                } else if (index == (values.size() - 1)) {
                    objective.getScore(trim(prep(values.get(0)))).setScore(lines.size() - key);
                    indexCache.put(key, 0);
                    stringCache.put(key, trim(prep(values.get(0))));
                }
            }
        }
    }

    private HashMap<Integer, List<String>> getLines() {
        ScoreBoard instance = ScoreBoard.getInstance();
        return instance.getBoardManager().getLines(player);
    }

    @Override
    public void run() {
        update();
    }

    private String prep(String string) {
        string = PlaceholderAPI.setPlaceholders(this.player, string);
        string = string.replace("{fill}", String.format("%-" + (size - 2) + "s", ""));
        string = string.replace("{server}", Core.getInstance().getServerName());
        return ChatColor.translateAlternateColorCodes('&', string).substring(0, Math.min(string.length(), 40));
    }

    private String trim(String string) {
        return string.substring(0, Math.min(string.length(), size));
    }
}
