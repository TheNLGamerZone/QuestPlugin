package nl.tim.questplugin.area;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
import java.util.UUID;

public class Cube extends Region
{
    private Location location1;
    private Location location2;

    public Cube(UUID uuid, Location location1, Location location2)
    {
        this(uuid, location1, location2, false);
    }

    public Cube(UUID uuid, Location location1, Location location2, boolean ignoreHeight)
    {
        super(ignoreHeight);

        this.uuid = uuid;
        this.location1 = location1;
        this.location2 = location2;
        this.world = location1.getWorld();
    }

    @Override
    public boolean inRegion(Location location, boolean ignoreHeight)
    {
        // Check if location is in the same world
        if (!location.getWorld().getName().equals(this.world.getName()))
        {
            return false;
        }

        // Get coords and compare
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

    public static Cube read(FileConfiguration configFile, UUID uuid)
    {
        return null;
    }

    @Override
    public void save(String internalPath)
    {

    }

    @Override
    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }

        if (!(object instanceof Cube))
        {
            return false;
        }

        Cube cube = (Cube) object;

        return new EqualsBuilder()
                .append(this.uuid, cube.uuid)
                .append(this.location1, cube.location1)
                .append(this.location2, cube.location2)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.uuid, this.location1, this.location2);
    }
}
