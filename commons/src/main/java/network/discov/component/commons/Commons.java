package network.discov.component.commons;

import network.discov.component.commons.command.*;
import network.discov.core.spigot.model.SpigotComponent;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Commons extends SpigotComponent implements Listener {
    private final HashMap<UUID, Date> coolDownMap = new HashMap<>();
    private static Commons instance;

    public Commons() {
        super("Commons");
        instance = this;
    }

    @Override
    public void onEnable() {
        setDefaultMessages();
        registerListener(this);
        registerCommand(new FlyCommand());
        registerCommand(new ClearInventoryCommand());
        registerCommand(new WakeCommand());
        registerCommand(new PlayerTimeCommand());
        registerCommand(new SpeedCommand());
    }

    @Override
    public void onDisable() {
        coolDownMap.clear();
    }

    void setDefaultMessages() {
        addDefaultMessage("inventory-cleared", "&eYour inventory has been cleared");
        addDefaultMessage("flight-enabled", "&aYou can fly! You can &ofly! &r&aYou can &lfly!");
        addDefaultMessage("flight-disabled", "&cFlight has been disabled");
        addDefaultMessage("player-time-set", "&aSet your time to: &b%s");
        addDefaultMessage("player-time-reset", "&eYour time has been synced with the server");
        addDefaultMessage("player-time-invalid", "&cThe specified time value is invalid.");
        addDefaultMessage("walk-speed-change", "&aWalking speed set to: &b%s");
        addDefaultMessage("fly-speed-change", "&aFlying speed set to: &b%s");
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();
        for (int i = 0; i < lines.length; i++) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(event.getLine(i))));
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer().hasPermission("core.commons.punch")) {
            if (event.getRightClicked() instanceof Player) {
                Player target = (Player) event.getRightClicked();
                launch(target);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    private boolean isMatchingWorld(World world) {
        for (String configWorld : getConfig().getStringList("punch-disabled-worlds")) {
            if (Bukkit.getWorld(configWorld) != null && world.getName().equalsIgnoreCase(configWorld)) {
                return true;
            }
        }
        return false;
    }

    private void launch(Player target) {
        if (!isMatchingWorld(target.getWorld())) {
            if (target.hasPermission("core.commons.punchable")) {
                Date now = new Date();
                if (coolDownMap.get(target.getUniqueId()) == null || now.compareTo(coolDownMap.get(target.getUniqueId())) > 0) {
                    coolDownMap.put(target.getUniqueId(), DateUtils.addSeconds(now, 5));
                    target.setVelocity(new Vector(0, 1, 0));
                }
            }
        }
    }

    public static Commons getInstance() {
        return instance;
    }
}
