package network.discov.component.playermonitor;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import network.discov.core.bungee.Core;
import network.discov.core.common.DatabaseConnector;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCache {
    private final Map<UUID, Long> sessions = new HashMap<>();
    private final DatabaseConnector connector;

    public PlayerCache() {
        connector = Core.getInstance().getDatabaseConnector();
        createTables();
    }

    private void createTables() {
        String sessionQuery = "CREATE TABLE IF NOT EXISTS pm_sessions\n" +
                "(\n" +
                "    ID          int        null,\n" +
                "    uuid      mediumtext null,\n" +
                "    time_login  timestamp  null,\n" +
                "    time_logout timestamp  null,\n" +
                "    seconds     int        null\n" +
                ");";

        String playersQuery = "CREATE TABLE IF NOT EXISTS pm_players\n" +
                "(\n" +
                "    UUID        mediumtext null,\n" +
                "    name        mediumtext null,\n" +
                "    first_login timestamp  null,\n" +
                "    last_seen   timestamp  null,\n" +
                "    ip_address  mediumtext null\n" +
                ");";

        try {
            connector.executeUpdate(sessionQuery);
            connector.executeUpdate(playersQuery);
        } catch (SQLException | ClassNotFoundException e) {
            PlayerMonitor.getInstance().getLogger().warning("Error occurred while creating database tables");
            e.printStackTrace();
        }
    }

    public void handlePlayerJoin(@NotNull ProxiedPlayer player) {
        sessions.put(player.getUniqueId(), new Date().getTime());
        savePlayer(player.getUniqueId(), player.getName(), player.getSocketAddress());
    }

    public void handlePlayerLeave(UUID uuid, String player, SocketAddress address) {
        saveSession(uuid);
        savePlayer(uuid, player, address);
        sessions.remove(uuid);
    }

    private String getIpAddress(@NotNull SocketAddress address) {
        return address.toString().replace("/", "").split(":")[0];
    }

    private void saveSession(UUID uuid) {
        Long login = sessions.get(uuid);
        long logout = new Date().getTime();
        if (login == null) {
            PlayerMonitor.getInstance().getLogger().warning(String.format("No login time found for player with UUID %s", uuid));
            return;
        }

        long seconds = (logout - login) / 1000;

        try {
            PreparedStatement statement = connector.prepareStatement("INSERT INTO pm_sessions (uuid, time_login, time_logout, seconds) VALUES (?, ?, ?, ?)");
            statement.setString(1, uuid.toString());
            statement.setTimestamp(2, new Timestamp(login));
            statement.setTimestamp(3, new Timestamp(logout));
            statement.setLong(4, seconds);
            statement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            PlayerMonitor.getInstance().getLogger().warning(String.format("Error saving session data for player with UUID %s", uuid));
        }

    }

    private void savePlayer(UUID uuid, String player, SocketAddress address) {
        String ipAddress = getIpAddress(address);
        Long login = sessions.get(uuid);
        Date now = new Date();
        if (login == null) {
            login = now.getTime();
        }

        try {
            PreparedStatement statement = connector.prepareStatement("INSERT INTO pm_players (UUID, name, first_login, last_seen, ip_address) VALUES (?, ?, ?, ?, ?) on DUPLICATE KEY UPDATE name = ?, ip_address = ?, last_seen = ?");
            statement.setString(1, uuid.toString());
            statement.setString(2, player);
            statement.setTimestamp(3, new Timestamp(login));
            statement.setTimestamp(4, new Timestamp(now.getTime()));
            statement.setString(5, ipAddress);
            statement.setString(6, player);
            statement.setString(7, ipAddress);
            statement.setTimestamp(8, new Timestamp(now.getTime()));
            statement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            PlayerMonitor.getInstance().getLogger().warning(String.format("Error saving player data for player with UUID %s", uuid));
        }
    }


}
