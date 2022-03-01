package network.discov.component.signatures;

import network.discov.component.signatures.command.SignatureCommandExecutor;
import network.discov.component.signatures.manager.RequestManager;
import network.discov.component.signatures.manager.SignatureManager;
import network.discov.core.spigot.model.SpigotComponent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class Signatures extends SpigotComponent implements Listener {
    private final RequestManager requestManager = new RequestManager();
    private final SignatureManager signatureManager;
    private static Signatures instance;

    public Signatures() {
        super("Signatures");
        instance = this;
        signatureManager = new SignatureManager();
    }

    @Override
    public void onEnable() {
        setDefaultMessages();
        signatureManager.loadSignatures();
        registerListener(this);
        registerExecutor(new SignatureCommandExecutor(this));
    }

    @Override
    public void onDisable() {
        requestManager.clearRequests();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        signatureManager.giveItem(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item.getType() == Material.WRITTEN_BOOK) {
            assert item.getItemMeta() != null;
            if (item.getItemMeta().getDisplayName().equals(getMessage("book-display-name"))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null && item.getType() == Material.WRITTEN_BOOK) {
            assert item.getItemMeta() != null;
            if (item.getItemMeta().getDisplayName().equals(getMessage("book-display-name"))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        ItemStack item = event.getOffHandItem();
        if (item != null && item.getType() == Material.WRITTEN_BOOK) {
            assert item.getItemMeta() != null;
            if (item.getItemMeta().getDisplayName().equals(getMessage("book-display-name"))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        ItemStack item = event.getCursor();
        if (item != null && item.getType() == Material.WRITTEN_BOOK) {
            assert item.getItemMeta() != null;
            if (item.getItemMeta().getDisplayName().equals(getMessage("book-display-name"))) {
                event.setCancelled(true);
            }
        }
    }
    
    private void setDefaultMessages() {
        addDefaultMessage("signature-request", "&7(&9&l!&7) %s &7want to send you their signature!");
        addDefaultMessage("signature-accept", "&8[&2Accept&8]");
        addDefaultMessage("signature-deny", "&8[&4Deny&8]");
        addDefaultMessage("request-accepted-sender", "&7Your signature for %s &7was &aaccepted");
        addDefaultMessage("request-accepted-receiver", "&7You &aaccepted &7the request from %s");
        addDefaultMessage("request-denied-sender", "&7Your signature for %s &7was &cdenied");
        addDefaultMessage("request-denied-receiver", "&7You &cdenied &7the request from %s");
        addDefaultMessage("request-not-found", "&7That request was not found. It might have been expired.");
        addDefaultMessage("signature-sent", "&7Signature request was sent to %s");
        addDefaultMessage("invalid-page-number", "&cInvalid page number provided!");
        addDefaultMessage("signature-removed", "&7The signature at page [&3%s&7] was successfully removed!");
        addDefaultMessage("book-display-name", "&7Signature Book");
        addDefaultMessage("book-lore", "Collect them all!");
        addDefaultMessage("book-first-page", "&9Welcome to your \n&6&lAutograph Book!\n\n&r&7&oYou can collect signatures by asking online &r&aStaff Members &7&oor &r&aCharacters &7&ofor an autograph.\n\nTo accept a signature request, click the &r&8[&2Accept&8] &7&obutton in chat.");
    }

    public int getBookSlot() {
        return getConfig().getInt("book-slot");
    }

    public SignatureManager getSignatureManager() {
        return signatureManager;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public static Signatures getInstance() {
        return instance;
    }
}
