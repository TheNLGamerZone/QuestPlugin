package nl.tim.questplugin.area;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class Cube extends Region
{
    private Location location1;
    private Location location2;

    public Cube(UUID uuid, Location location1, Location location2)
    {
        this.uuid = uuid;
        this.location1 = location1;
        this.location2 = location2;
    }

    @Override
    public boolean inRegion(Location location, boolean ignoreHeight)
    {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return liesBetween(this.location1.getX(), this.location2.getX(), x)
                && liesBetween(this.location1.getY(), this.location2.getY(), y)
                && (ignoreHeight || liesBetween(this.location1.getZ(), this.location2.getZ(), z));
    }

    private boolean liesBetween(double a, double b, double n)
    {
        return (n - a) * (n - b) <= 0;
    }

    public static Cube read(FileConfiguration configFile)
    {
        return null;
    }

    @Override
    public void save(FileConfiguration dataFile) {

    }
}
