package network.discov.component.chat;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import network.discov.component.chat.model.PrivateMessageEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) { return; }
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        //TODO: Check if message is command

        //TODO: Chat mute check

        //TODO: Chat filter

        //TODO: Mentions
    }

    @EventHandler
    public void onPrivateMessage(PrivateMessageEvent event) {
        //TODO: Implement
    }
}
