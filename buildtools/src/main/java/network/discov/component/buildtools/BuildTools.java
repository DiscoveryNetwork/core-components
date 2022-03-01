package network.discov.component.buildtools;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import network.discov.component.buildtools.command.BuildCommand;
import network.discov.component.buildtools.command.OutlinesCommandExecutor;
import network.discov.component.buildtools.manager.BuildModeManager;
import network.discov.component.buildtools.manager.ReferenceManager;
import network.discov.component.buildtools.manager.SelectionManager;
import network.discov.core.spigot.model.SpigotComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class BuildTools extends SpigotComponent implements Listener {
    private final BuildModeManager buildModeManager = new BuildModeManager();
    private final SelectionManager selectionManager = new SelectionManager();
    private final ReferenceManager referenceManager;
    private static BuildTools instance;

    public BuildTools() {
        super("BuildTools");
        instance = this;
        referenceManager = new ReferenceManager();
    }

    @Override
    public void onEnable() {
        setDefaultMessages();
        registerCommand(new BuildCommand());
        registerExecutor(new OutlinesCommandExecutor(this));
        registerListener(this);
        getScheduler().runTask(referenceManager::load);
    }

    @Override
    public void onDisable() {
        buildModeManager.disableAll();
        referenceManager.removeHolograms();
    }

    private void setDefaultMessages() {
        addDefaultMessage("buildmode-enabled", "&aYou have enabled &eBuild Mode!");
        addDefaultMessage("buildmode-disabled", "&cYou have disabled &eBuild Mode!");
        addDefaultMessage("outlines-reference-create", "&7You created a reference point [&3%s&7] &7at your current location");
        addDefaultMessage("outlines-reference-remove", "&7You removed the reference point [&3%s&7]");
        addDefaultMessage("outlines-reference-not-found", "&cThe specified reference point &7[&3%s&7] &cdoesn't exist!");
        addDefaultMessage("invalid-page-number", "&cThe specified page number &7[&3%s&7] &cis invalid!");
        addDefaultMessage("outlines-reference-list-header", "&f+---+ &aReference Points &7(page %s) &f+---+");
        addDefaultMessage("outlines-reference-list-item", "&6%s &7(&3%s %s %s&7)");
        addDefaultMessage("outlines-reference-list-footer", "&3Type '/outlines list %s` to see more.");
        addDefaultMessage("outlines-reference-selected", "&7You selected the reference point [&3%s&7]");
        addDefaultMessage("outlines-reference-selection-cleared", "&7Cleared your reference point selection");
        addDefaultMessage("outlines-block-place", "&7Block placed at [&3%s&7, &3%s&7, &3%s&7]");
        addDefaultMessage("outlines-no-reference-selected", "&cPlease select a reference point before placing a block!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (buildModeManager.hasPlayer(player)) {
            buildModeManager.disable(event.getPlayer());
        }
    }

    public HashMap<Integer, ItemStack> getBuildItems() {
        HashMap<Integer, ItemStack> items = new HashMap<>();
        ConfigurationSection section = BuildTools.getInstance().getConfig().getConfigurationSection("build-inventory");
        assert section != null;
        for (String key : section.getKeys(false)) {
            Material material = Material.getMaterial(Objects.requireNonNull(section.getString(key)));
            if (material != null) {
                items.put(Integer.valueOf(key), new ItemStack(material, 1));
            }
        }
        return items;
    }

    public @Nullable User getLuckPermsUser(Player player) {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            return LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        }
        return null;
    }

    public BuildModeManager getBuildModeManager() {
        return buildModeManager;
    }

    public ReferenceManager getReferenceManager() {
        return referenceManager;
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public static BuildTools getInstance() {
        return instance;
    }
}
