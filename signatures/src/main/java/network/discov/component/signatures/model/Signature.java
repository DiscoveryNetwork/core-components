package network.discov.component.signatures.model;

import org.bukkit.entity.Player;

import java.util.UUID;

public class Signature {
    private final UUID owner;
    private final String content;

    public Signature(Player owner, String content) {
        this.owner = owner.getUniqueId();
        this.content = content;
    }

    public Signature(UUID owner, String content) {
        this.owner = owner;
        this.content = content;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getContent() {
        return content;
    }
}
