package network.discov.component.buildtools.manager;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import network.discov.component.buildtools.BuildTools;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class BuildModeManager {
    private final HashMap<UUID, ItemStack[]> inventories = new HashMap<>();

    public void disableAll() {
        for (UUID uuid : inventories.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                disable(player);
                player.sendMessage(BuildTools.getInstance().getMessage("buildmode-disabled"));
            }
        }
        inventories.clear();
    }

    public void enable(Player player) {
        giveBuildModeInventory(player);
        player.setGameMode(GameMode.CREATIVE);
        BuildTools.getInstance().getReferenceManager().showHolograms(player);

        User user = BuildTools.getInstance().getLuckPermsUser(player);
        if (user != null) {
            user.data().add(Node.builder("group.buildmode").build());
        }
    }

    public void disable(Player player) {
        restoreInventory(player);
        player.setGameMode(GameMode.ADVENTURE);
        BuildTools.getInstance().getReferenceManager().hideHolograms(player);

        User user = BuildTools.getInstance().getLuckPermsUser(player);
        if (user != null) {
            user.data().remove(Node.builder("group.buildmode").build());
        }
    }

    public boolean hasPlayer(Player player) {
        return inventories.containsKey(player.getUniqueId());
    }

    private void giveBuildModeInventory(Player player) {
        inventories.put(player.getUniqueId(), player.getInventory().getContents());
        Inventory buildInventory = Bukkit.createInventory(player, InventoryType.PLAYER);
        HashMap<Integer, ItemStack> buildItems = BuildTools.getInstance().getBuildItems();
        for (Integer key : buildItems.keySet()) {
            buildInventory.setItem(key, buildItems.get(key));
        }
        player.getInventory().setContents(buildInventory.getContents());
    }

    private void restoreInventory(Player player) {
        if (inventories.containsKey(player.getUniqueId())) {
            player.getInventory().setContents(inventories.get(player.getUniqueId()));
            inventories.remove(player.getUniqueId());
        }
    }
}
