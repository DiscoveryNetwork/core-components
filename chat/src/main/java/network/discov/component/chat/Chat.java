package network.discov.component.chat;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import network.discov.core.bungee.model.BungeeComponent;

import java.io.InputStream;

public class Chat extends BungeeComponent {
    private static Chat instance;

    public Chat() {
        instance = this;
    }

    @Override
    public void onEnable() {
        setDefaultMessages();
        registerListener(new ChatListener());
    }

    @Override
    public void onDisable() {

    }

    private void setDefaultMessages() {
        loadMessagesFromFile("chat-formats.yml");
        loadMessagesFromFile("chat-messages.yml");
    }

    private void loadMessagesFromFile(String fileName) {
        InputStream stream = getResourceFile(fileName);
        Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(stream);
        for (String key : config.getKeys()) {
            addDefaultMessage(key, config.getString(key));
        }
    }

    public static Chat getInstance() {
        return instance;
    }
}
