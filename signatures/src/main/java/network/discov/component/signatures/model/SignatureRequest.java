package network.discov.component.signatures.model;

import org.bukkit.entity.Player;

import java.util.UUID;

public class SignatureRequest {
    private final UUID uuid = UUID.randomUUID();
    private final Player sender;
    private final Player receiver;
    private final String content;

    public SignatureRequest(Player sender, Player receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }
}
