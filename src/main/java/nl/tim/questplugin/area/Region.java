package nl.tim.questplugin.area;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public abstract class Region
{
    // Constants
    public static final String ID_CUBE = "911527db";
    public static final String ID_SPHERE = "2ad05917";
    public static final String ID_POLYGON = "0d403ffa";

    // Regular fields
    String regionFileIdentifier;
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

    public abstract boolean equals(Object object);

    public abstract int hashCode();
}
