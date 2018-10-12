package nl.tim.questplugin.area;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public abstract class Region
{
    UUID uuid;
    World world;
    boolean ignoreHeight;

    public Region(boolean ignoreHeight)
    {
        this.ignoreHeight = ignoreHeight;
    }

    public boolean inRegion(Location location)
    {
        return inRegion(location, ignoreHeight);
    }

    public abstract boolean inRegion(Location location, boolean ignoreHeight);

    public abstract void save(String internalPath);

    public abstract boolean equals(Object object);

    public abstract int hashCode();
}
