package nl.tim.questplugin.area;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Set;
import java.util.UUID;

public class Polygon extends Region
{
    private Set<Location> locationSet;

    private double xMin = 0;
    private double xMax = 0;
    private double yMin = 0;
    private double yMax = 0;

    public Polygon(UUID uuid, Set<Location> locationSet)
    {
        this.uuid = uuid;
        this.locationSet = locationSet;

        this.calcBorders();
    }

    private void calcBorders()
    {
        for (Location location : this.locationSet)
        {
            double x = location.getX();
            double y = location.getY();

            this.xMin = x < this.xMin ? x : this.xMin;
            this.xMax = x > this.xMax ? x : this.xMax;
            this.yMin = y < this.yMin ? y : this.yMin;
            this.yMax = y > this.yMax ? y : this.yMax;
        }
    }

    @Override
    public boolean inRegion(Location location, boolean ignoreHeight)
    {
        // To reduce complexity we ignore height for polygons
        // Since precision is not that important here I'll use ray casting



        return false;
    }

    @Override
    public void save(FileConfiguration dataFile) {

    }
}
