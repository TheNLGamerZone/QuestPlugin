package nl.tim.questplugin.area;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class Sphere extends Region
{
    private Location center;
    private double radius;

    public Sphere(UUID uuid, Location center, double radius)
    {
        this.uuid = uuid;
        this.center = center;
        this.radius = radius;
    }

    @Override
    public boolean inRegion(Location location, boolean ignoreHeight)
    {
        // Calculate differences so we can plug them in pythagoras
        double dx = this.center.getX() - location.getX();
        double dy = this.center.getY() - location.getY();
        double dz = ignoreHeight  ? 0.0 : this.center.getZ() - location.getZ();

        // Skip sqrt by squaring radius
        double distanceToCenter = dx * dx + dy * dy + dz * dz;

        return distanceToCenter <= this.radius * this.radius;
    }

    @Override
    public void save(FileConfiguration dataFile)
    {

    }

    public static Sphere read(FileConfiguration dataFile, String path)
    {
        return null;
    }
}
