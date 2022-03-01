package network.discov.component.buildtools.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class ReferencePoint implements Serializable {
    private final String name;
    private final double x;
    private final double y;
    private final double z;
    private final String world;

    public ReferencePoint(String name, @NotNull Location location) {
        this.name = name;
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.world = Objects.requireNonNull(location.getWorld()).getName();
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(this.world);
        return new Location(world, x, y, z);
    }
}
