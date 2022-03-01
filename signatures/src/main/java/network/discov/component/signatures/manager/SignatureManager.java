package network.discov.component.signatures.manager;

import network.discov.component.signatures.model.Signature;
import network.discov.component.signatures.model.SignatureRequest;
import network.discov.core.commons.DatabaseConnector;
import network.discov.core.spigot.Core;
import network.discov.component.signatures.Signatures;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SignatureManager {
    private final HashMap<UUID, List<Signature>> signatures = new HashMap<>();
    private final DatabaseConnector connector;

    public SignatureManager() {
        connector = Core.getInstance().getDatabaseConnector();
        if (connector == null) {
            Signatures.getInstance().getLogger().severe("Database connector not initialized! Cannot save reference points.");
            return;
        }

        String query = "CREATE TABLE IF NOT EXISTS signatures\n" +
                "(\n" +
                "    ID        int auto_increment\n" +
                "        primary key,\n" +
                "    owner     varchar(36) null,\n" +
                "    signature text        null\n" +
                ");";
        try {
            connector.executeUpdate(query);
        } catch (SQLException | ClassNotFoundException e) {
            Signatures.getInstance().getLogger().warning("Error occurred while creating database table signatures");
            e.printStackTrace();
        }
    }

    public void addSignature(SignatureRequest request) {
        signatures.computeIfAbsent(request.getReceiver().getUniqueId(), k -> new ArrayList<>());

        Signature signature = new Signature(request.getReceiver(), request.getContent());
        signatures.get(request.getReceiver().getUniqueId()).add(signature);
        giveItem(request.getReceiver());
        save(signature);
    }

    public void removeSignature(Player player, int pageNumber) {
        signatures.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());

        try {
            Signature signature = signatures.get(player.getUniqueId()).get(pageNumber - 2);
            signatures.get(player.getUniqueId()).remove(pageNumber - 2);
            giveItem(player);
            delete(signature);
        } catch (IndexOutOfBoundsException ignored) {}
    }

    public List<Integer> getValidPageNumbers(Player player) {
        if (!signatures.containsKey(player.getUniqueId())) { return new ArrayList<>(); }

        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = 1; i < (signatures.get(player.getUniqueId()).size() + 1); i++) {
            pageNumbers.add(i);
        }
        return pageNumbers;
    }

    private List<Signature> getSignatures(Player player) {
        signatures.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
        return signatures.get(player.getUniqueId());
    }

    public void giveItem(Player player) {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        item.setItemMeta(getItemMeta(player, item));
        player.getInventory().setItem(Signatures.getInstance().getBookSlot(), item);
    }

    private BookMeta getItemMeta(Player player, ItemStack item) {
        BookMeta meta = (BookMeta) item.getItemMeta();
        assert meta != null;

        Signatures component = Signatures.getInstance();
        meta.setDisplayName(component.getMessage("book-display-name"));
        meta.setTitle("Book");
        meta.setLore(Collections.singletonList(component.getMessage("book-lore")));
        meta.setAuthor(player.getName());
        meta.addPage(component.getMessage("book-first-page"));

        for (Signature signature : getSignatures(player)) {
            meta.addPage(signature.getContent());
        }
        return meta;
    }

    private void save(Signature signature) {
        assert connector != null;

        Signatures.getInstance().getScheduler().runTaskAsynchronously(() -> {
            try {
                PreparedStatement statement = connector.prepareStatement("INSERT INTO signatures (owner, signature) VALUES (?, ?)");
                statement.setString(1, signature.getOwner().toString());
                statement.setString(2, signature.getContent());
                statement.execute();
                Signatures.getInstance().getLogger().info("Successfully saved signature to database.");
            } catch (SQLException | ClassNotFoundException e) {
                Signatures.getInstance().getLogger().warning("Error occurred while saving signature.");
                e.printStackTrace();
            }
        });
    }

    private void delete(Signature signature) {
        assert connector != null;

        Signatures.getInstance().getScheduler().runTaskAsynchronously(() -> {
            try {
                PreparedStatement statement = connector.prepareStatement("DELETE FROM signatures WHERE signature = ?");
                statement.setString(1, signature.getContent());
                statement.execute();
                Signatures.getInstance().getLogger().info("Successfully deleted signature from database.");
            } catch (SQLException | ClassNotFoundException e) {
                Signatures.getInstance().getLogger().warning("Error occurred while deleting signature.");
                e.printStackTrace();
            }
        });
    }

    public void loadSignatures() {
        assert connector != null;

        Signatures.getInstance().getScheduler().runTaskAsynchronously(() -> {
            int count = 0;
            try {
                ResultSet result = connector.executeStatement("SELECT * FROM signatures");
                while (result.next()) {
                    UUID owner = UUID.fromString(result.getString("owner"));
                    Signature signature = new Signature(owner, result.getString("signature"));
                    if (!signatures.containsKey(owner)) {
                        signatures.put(owner, new ArrayList<>());
                    }
                    count += 1;
                    signatures.get(owner).add(signature);
                }
                Signatures.getInstance().getLogger().info(String.format("Successfully loaded %s signatures", count));
            } catch (SQLException | ClassNotFoundException e) {
                Signatures.getInstance().getLogger().warning("Error occurred while loading signatures");
                e.printStackTrace();
            }
        });
    }
}
