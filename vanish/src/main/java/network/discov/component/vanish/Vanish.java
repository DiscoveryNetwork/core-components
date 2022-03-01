package network.discov.component.vanish;

import network.discov.core.spigot.Core;
import network.discov.core.spigot.model.SpigotComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Vanish extends SpigotComponent implements Listener {
    private final List<UUID> vanishedPlayers = new ArrayList<>();
    private final VanishExpansion expansion = new VanishExpansion();
    private static Vanish instance;

    public Vanish() {
        super("Vanish");
        instance = this;
    }

    @Override
    public void onEnable() {
        setDefaultMessages();
        registerListener(this);
        registerCommand(new VanishCommand());
        expansion.register();
    }

    @Override
    public void onDisable() {
        for (UUID uuid : vanishedPlayers) {
            Player player = Bukkit.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                showPlayer(player, false, false);
            }
        }
        vanishedPlayers.clear();
        expansion.unregister();
    }

    private void setDefaultMessages() {
        addDefaultMessage("player-vanish", "&e&oWait... Where have you gone? &r&aYou are now invisible!");
        addDefaultMessage("player-unvanish", "&e&oFound ya! &r&cYou are no longer invisible!");
        addDefaultMessage("announce-vanish", "&f[&c&lStaff&r&f] &b%s &eis now invisible");
        addDefaultMessage("announce-unvanish", "&f[&c&lStaff&r&f] &b%s &eis now visible");
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
       checkPlayerVanish(event.getPlayer());
        if (event.getPlayer().hasPermission("core.component.vanish.bypass")) { return; }
        for (UUID uuid : vanishedPlayers) {
            Player hiddenPlayer = Bukkit.getPlayer(uuid);
            assert hiddenPlayer != null;
            event.getPlayer().hidePlayer(Core.getInstance(), hiddenPlayer);
        }
    }

    private void checkPlayerVanish(@NotNull Player player) {
        if (Boolean.parseBoolean(getPersistentStorage().getPlayerValue(player.getUniqueId(), "Vanish"))) {
            if (isPlayerHidden(player)) { return; }
            getLogger().info(String.format("Player %s has vanish enabled through Redis.", player.getName()));
            hidePlayer(player, false);
        }
    }

    private void updatePlayerVanish(@NotNull Player player, boolean vanished) {
        getPersistentStorage().setPlayerValue(player.getUniqueId(), "Vanish", String.valueOf(vanished));
    }

    public void hidePlayer(Player player, boolean announce) {
        updatePlayerVanish(player, true);
        if (!vanishedPlayers.contains(player.getUniqueId())) {
            vanishedPlayers.add(player.getUniqueId());
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == player) { continue; }
            if (onlinePlayer.hasPermission("core.component.vanish.bypass")) { continue; }
            onlinePlayer.hidePlayer(Core.getInstance(), player);
        }
        if (announce) {
            Core.getInstance().broadcast(getMessage("announce-vanish", player.getName()), "core.group.staff");
        }
    }

    public void showPlayer(Player player, boolean announce, boolean remove) {
        updatePlayerVanish(player, false);
        if (vanishedPlayers.contains(player.getUniqueId())) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer == player) { continue; }
                onlinePlayer.showPlayer(Core.getInstance(), player);
            }
            if (remove) {
                vanishedPlayers.remove(player.getUniqueId());
            }
            if (announce) {
                Core.getInstance().broadcast(getMessage("announce-unvanish", player.getName()), "core.group.staff");
            }
        }
    }

    public boolean isPlayerHidden(@NotNull Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public static Vanish getInstance() {
        return instance;
    }
}
