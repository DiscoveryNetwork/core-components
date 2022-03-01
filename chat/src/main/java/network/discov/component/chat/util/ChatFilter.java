package network.discov.component.chat.util;

import network.discov.component.chat.Chat;
import network.discov.core.bungee.Core;
import network.discov.core.common.DatabaseConnector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFilter {
    private final List<String> bannedWords = new ArrayList<>();
    private final List<String> excludedWords = new ArrayList<>();
    private final HashMap<String, String> replacements = new HashMap<>();
    private final DatabaseConnector connector;

    public ChatFilter() {
        connector = Core.getInstance().getDatabaseConnector();
        assert connector != null;
        try {
            connector.executeUpdate("CREATE TABLE IF NOT EXISTS chat_banned\n" +
                    "(\n" +
                    "    ID      int auto_increment\n" +
                    "        primary key,\n" +
                    "    `match` varchar(50) null\n" +
                    ");");
            connector.executeUpdate("CREATE TABLE IF NOT EXISTS chat_exclusions\n" +
                    "(\n" +
                    "    ID      int auto_increment\n" +
                    "        primary key,\n" +
                    "    `match` varchar(50) null\n" +
                    ");");
            connector.executeUpdate("CREATE TABLE IF NOT EXISTS chat_replacements\n" +
                    "(\n" +
                    "    ID          int auto_increment\n" +
                    "        primary key,\n" +
                    "    `match`     varchar(50) null,\n" +
                    "    replacement varchar(50) null\n" +
                    ");");
        } catch (SQLException | ClassNotFoundException e) {
            Chat.getInstance().getLogger().warning("An error occurred while creating tables for ChatFilter.");
            e.printStackTrace();
        }
    }

    public boolean hasForbidden(String message) {
        String[] words = message.split(" ");
        List<String> matches = new ArrayList<>();
        List<String> finalMatches = new ArrayList<>();

        for (String word : words) {
            if (bannedWords.stream().anyMatch(word.toLowerCase()::contains)) {
                matches.add(word);
            }
        }

        for (String match : matches) {
            if (excludedWords.stream().noneMatch(match.toLowerCase()::contains)) {
                finalMatches.add(match);
            }
        }

        return !finalMatches.isEmpty();
    }

    public String parseReplacements(String message) {
        for (String match : replacements.keySet()) {
            if (message.toLowerCase().contains(match)) {
                message = message.replaceAll("(?i)" + match, replacements.get(match));
            }
        }
        return message;
    }

    public void load() {
        assert connector != null;

        Chat.getInstance().getScheduler().runAsync(() -> {

        });
    }
}
