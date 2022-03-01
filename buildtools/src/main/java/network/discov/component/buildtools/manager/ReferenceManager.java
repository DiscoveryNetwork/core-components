package network.discov.component.buildtools.manager;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import network.discov.component.buildtools.BuildTools;
import network.discov.component.buildtools.model.ReferencePoint;
import network.discov.core.commons.DatabaseConnector;
import network.discov.core.spigot.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReferenceManager {
    private final HashMap<String, ReferencePoint> referencePoints = new HashMap<>();
    private final HashMap<String, Hologram> holograms = new HashMap<>();
    private final DatabaseConnector connector;

    public ReferenceManager() {
        connector = Core.getInstance().getDatabaseConnector();
        if (connector == null) {
            BuildTools.getInstance().getLogger().severe("Database connector not initialized! Cannot save reference points.");
            return;
        }

        String query = "CREATE TABLE IF NOT EXISTS reference_points\n" +
                "(\n" +
                "    ID     int auto_increment\n" +
                "        primary key,\n" +
                "    server text   null,\n" +
                "    name   text   null,\n" +
                "    world  text   null,\n" +
                "    x      double null,\n" +
                "    y      double null,\n" +
                "    z      double null\n" +
                ");";
        try {
            connector.executeUpdate(query);
        } catch (SQLException | ClassNotFoundException e) {
            BuildTools.getInstance().getLogger().warning("Error occurred while creating database table reference_points");
            e.printStackTrace();
        }
    }

    public void addPoint(String name, Location location) {
        ReferencePoint point = new ReferencePoint(name, location);
        createHologram(point);
        referencePoints.put(name.toLowerCase(), point);
        save(point);
    }

    public boolean removePoint(String name) {
        if (referencePoints.containsKey(name.toLowerCase())) {
            removeHologram(referencePoints.get(name.toLowerCase()));
            referencePoints.remove(name.toLowerCase());
            delete(name.toLowerCase());
            return true;
        }
        return false;
    }

    public ReferencePoint getPoint(String name) {
        return referencePoints.get(name.toLowerCase());
    }

    public List<ReferencePoint> getPoints() {
        return new ArrayList<>(referencePoints.values());
    }

    public void load() {
        assert connector != null;

        BuildTools.getInstance().getScheduler().runTaskAsynchronously(() -> {
            try {
                PreparedStatement statement = connector.prepareStatement("SELECT * FROM reference_points WHERE server = ?");
                statement.setString(1, Core.getInstance().getServerName());
                statement.execute();
                ResultSet result = statement.getResultSet();
                while (result.next()) {
                    World world = Bukkit.getWorld(result.getString("world"));
                    Location location = new Location(world, result.getDouble("x"), result.getDouble("y"), result.getDouble("z"));
                    ReferencePoint referencePoint = new ReferencePoint(result.getString("name"), location);
                    referencePoints.put(result.getString("name"), referencePoint);
                    createHologram(referencePoint);
                }
                BuildTools.getInstance().getLogger().info("Loaded " + referencePoints.size() + " reference points.");
            } catch (SQLException | ClassNotFoundException e) {
                BuildTools.getInstance().getLogger().warning("Error occurred while loading reference points");
                e.printStackTrace();
            }
        });
    }

    private void save(ReferencePoint referencePoint) {
        assert connector != null;

        BuildTools.getInstance().getScheduler().runTaskAsynchronously(() -> {
            try {
                PreparedStatement statement = connector.prepareStatement("INSERT INTO reference_points (server, name, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?)");
                Location location = referencePoint.getLocation();
                assert location.getWorld() != null;
                statement.setString(1, Core.getInstance().getServerName());
                statement.setString(2, referencePoint.getName());
                statement.setString(3, location.getWorld().getName());
                statement.setDouble(4, location.getX());
                statement.setDouble(5, location.getY());
                statement.setDouble(6, location.getZ());
                statement.execute();
                BuildTools.getInstance().getLogger().info(String.format("Successfully saved ReferencePoint [%s] to database.", referencePoint.getName()));
            } catch (SQLException | ClassNotFoundException e) {
                BuildTools.getInstance().getLogger().warning("Error occurred while saving reference point: " + referencePoint.getName());
                e.printStackTrace();
            }
        });
    }

    private void delete(String name) {
        assert connector != null;

        BuildTools.getInstance().getScheduler().runTaskAsynchronously(() -> {
            try {
                PreparedStatement statement = connector.prepareStatement("DELETE FROM reference_points WHERE name = ? AND server = ?");
                statement.setString(1, name);
                statement.setString(2, Core.getInstance().getServerName());
                statement.execute();
                BuildTools.getInstance().getLogger().info(String.format("Successfully deleted ReferencePoint [%s] from database.", name));
            } catch (SQLException | ClassNotFoundException e) {
                BuildTools.getInstance().getLogger().warning("Error occurred while deleting reference point: " + name);
                e.printStackTrace();
            }
        });
    }

    public void removeHolograms() {
        for (ReferencePoint referencePoint : referencePoints.values()) {
            removeHologram(referencePoint);
        }
    }

    private void createHologram(ReferencePoint point) {
        BuildTools.getInstance().getScheduler().runTask(() -> {
            final Hologram hologram = HologramsAPI.createHologram(Core.getInstance(), point.getLocation().add(0, 2, 0));
            hologram.appendTextLine("§c§lReference Point").setTouchHandler(getTouchHandler(hologram));
            hologram.appendTextLine("§7" + point.getName()).setTouchHandler(getTouchHandler(hologram));
            hologram.getVisibilityManager().setVisibleByDefault(false);
            hologram.getVisibilityManager().resetVisibilityAll();
            holograms.put(point.getName().toLowerCase(), hologram);
        });
    }

    private void removeHologram(ReferencePoint point) {
        holograms.get(point.getName().toLowerCase()).delete();
        holograms.remove(point.getName().toLowerCase());
    }

    public void showHolograms(Player player) {
        for (Hologram hologram : holograms.values()) {
            hologram.getVisibilityManager().showTo(player);
        }
    }

    public void hideHolograms(Player player) {
        for (Hologram hologram : holograms.values()) {
            hologram.getVisibilityManager().hideTo(player);
        }
    }

    private TouchHandler getTouchHandler(Hologram hologram) {
        return player -> {
            String name = ChatColor.stripColor(((TextLine) hologram.getLine(1)).getText());
            ReferencePoint point = getPoint(name);
            BuildTools.getInstance().getSelectionManager().select(player, point);
            player.sendMessage("§7Selected the point §6" + point.getName());
        };
    }
}
