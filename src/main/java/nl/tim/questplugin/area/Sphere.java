package nl.tim.questplugin.area;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
import java.util.UUID;

public class Sphere extends Region
{
    private Location center;
    private double radius;

    public Sphere(UUID uuid, Location center, double radius)
    {
        this(uuid, center, radius, false);
    }

    public Sphere(UUID uuid, Location center, double radius, boolean ignoreHeight)
    {
        super(ignoreHeight);

        this.uuid = uuid;
        this.center = center;
        this.radius = radius;
        this.world = center.getWorld();
    }

    @Override
    public boolean inRegion(Location location, boolean ignoreHeight)
    {
        // Check if location is in the same world
        if (!location.getWorld().getName().equals(this.world.getName()))
        {
            return false;
        }

        // Calculate differences so we can plug them in pythagoras
        double dx = this.center.getX() - location.getX();
        double dy = this.center.getY() - location.getY();
        double dz = ignoreHeight  ? 0.0 : this.center.getZ() - location.getZ();

        // Skip sqrt by squaring radius
        double distanceToCenter = dx * dx + dy * dy + dz * dz;

        return distanceToCenter <= this.radius * this.radius;
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

        if (!(object instanceof Sphere))
        {
            return false;
        }

        Sphere sphere = (Sphere) object;

        return new EqualsBuilder()
                .append(this.uuid, sphere.uuid)
                .append(this.center, sphere.center)
                .append(this.radius, sphere.radius)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.uuid, this.center, this.radius);
    }

    public static Sphere read(FileConfiguration dataFile, String path)
    {
        return null;
    }
}
