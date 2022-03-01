package network.discov.component.maintenance;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import network.discov.core.bungee.model.BungeeComponent;

public class MaintenanceMode extends BungeeComponent implements Listener {
    private static MaintenanceMode instance;
    private boolean enabled = false;

    public MaintenanceMode() {
        super("MaintenanceMode");
        instance = this;
    }

    @Override
    public void onEnable() {
        setDefaultMessages();
        enabled = getRedisValue();
        registerListener(this);
        registerCommand(new MaintenanceCommand());
    }

    @Override
    public void onDisable() {

    }

    private void setDefaultMessages() {
        addDefaultMessage("server-list-description", "We are currently under maintenance. See you soon!");
        addDefaultMessage("kick-message", "Maintenance Mode Enabled");
        addDefaultMessage("enabled", "&7Maintenance mode is now &aenabled");
        addDefaultMessage("disabled", "&7Maintenance mode is now &cdisabled");
    }

    @EventHandler(priority = 64)
    public void onProxyPing(ProxyPingEvent event) {
        if (enabled) {
            event.getResponse().setDescriptionComponent(new TextComponent(getMessage("server-list-description")));
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        if (enabled && !event.getPlayer().hasPermission("core.maintenance.bypass")) {
            event.getPlayer().disconnect(new TextComponent(getMessage("kick-message")));
        }
    }

    public void enable() {
        enabled = true;
        getPersistentStorage().setValue("MaintenanceMode", String.valueOf(true));
    }

    public void disable() {
        enabled = false;
        getPersistentStorage().setValue("MaintenanceMode", String.valueOf(false));
    }

    private boolean getRedisValue() {
        String value = getPersistentStorage().getValue("MaintenanceMode");
        if (value == null || value.isEmpty()) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    public static MaintenanceMode getInstance() {
        return instance;
    }
}
