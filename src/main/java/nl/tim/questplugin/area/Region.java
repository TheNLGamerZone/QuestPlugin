package nl.tim.questplugin.area;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public abstract class Region
{
    UUID uuid;

    public abstract boolean inRegion(Location location, boolean ignoreHeight);

    public abstract void save(FileConfiguration dataFile);
}
