package network.discov.component.playermonitor;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import network.discov.component.playermonitor.command.CheckTimeCommand;
import network.discov.component.playermonitor.command.OnlineTimeCommand;
import network.discov.component.playermonitor.command.SeenCommand;
import network.discov.core.bungee.Core;
import network.discov.core.bungee.model.BungeeComponent;

public class PlayerMonitor extends BungeeComponent implements Listener {
    @Getter private static PlayerMonitor instance;
    @Getter private static PlayerCache playerCache;

    public PlayerMonitor() {
        super("PlayerMonitor");
        instance = this;
    }

    @Override
    public void onEnable() {
        if (Core.getInstance().getDatabaseConnector() == null) {
            getLogger().severe("Core DB connector is not initialized! Disabling component...");
            return;
        }
        playerCache = new PlayerCache();
        registerListener(this);
        registerCommand(new CheckTimeCommand());
        registerCommand(new OnlineTimeCommand());
        registerCommand(new SeenCommand());
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        getScheduler().runAsync(() -> playerCache.handlePlayerJoin(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        getScheduler().runAsync(() -> playerCache.handlePlayerLeave(player.getUniqueId(), player.getName(), player.getSocketAddress()));
    }
}
