package network.discov.component.vanish;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class VanishExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "cv";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ParrotLync";
    }

    @Override
    public @NotNull String getVersion() {
        return Vanish.getInstance().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.equalsIgnoreCase("vanished")) {
            assert player.getPlayer() != null;
            return String.valueOf(Vanish.getInstance().isPlayerHidden(player.getPlayer()));
        }

        return null;
    }
}
