package network.discov.component.scoreboard;

import network.discov.core.spigot.model.SpigotComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;

public class ScoreBoard extends SpigotComponent implements Listener {
    private BoardManager boardManager;
    private static ScoreBoard instance;

    public ScoreBoard() {
        super("ScoreBoard");
        instance = this;
    }

    @Override
    public void onEnable() {
        registerListener(this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Dependency PlaceholderAPI was found. Enabling scoreboard...");

            // Get visitor lines
            HashMap<Integer, List<String>> visitorLines = new HashMap<>();
            ConfigurationSection visitorContent = getConfig().getConfigurationSection("visitor-scoreboard");
            assert visitorContent != null;
            for (String key : visitorContent.getKeys(false)) {
                visitorLines.put(Integer.parseInt(key), visitorContent.getStringList(key));
            }

            // Get staff lines
            HashMap<Integer, List<String>> staffLines = new HashMap<>();
            ConfigurationSection staffContent = getConfig().getConfigurationSection("staff-scoreboard");
            assert staffContent != null;
            for (String key : staffContent.getKeys(false)) {
                staffLines.put(Integer.parseInt(key), staffContent.getStringList(key));
            }

            boardManager = new BoardManager(visitorLines, staffLines);
            for (Player player : Bukkit.getOnlinePlayers()) {
                boardManager.init(player);
            }
        }
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            boardManager.remove(player);
        }
    }

    public Integer getInterval() {
        return getConfig().getInt("interval");
    }

    public Integer getMaxLength() {
        return getConfig().getInt("max-length");
    }

    public String getTitle() {
        return getConfig().getString("title");
    }

    public BoardManager getBoardManager() {
        return boardManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boardManager.init(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        boardManager.remove(event.getPlayer());
    }

    public static ScoreBoard getInstance() {
        return instance;
    }
}
