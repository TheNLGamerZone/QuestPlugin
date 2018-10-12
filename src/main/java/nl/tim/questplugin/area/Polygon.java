package nl.tim.questplugin.area;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.geom.Line2D;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.UUID;

public class Polygon extends Region
{
    protected class Line
    {
        Point startPoint;
        Point endPoint;

        Line(Location startLocation, Location endLocation)
        {
            this.startPoint = new Point(startLocation.getX(), startLocation.getY());
            this.endPoint = new Point(endLocation.getX(), endLocation.getY());
        }

        boolean intersect(Line line)
        {
            return Line2D.linesIntersect(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                    line.startPoint.x, line.startPoint.y, line.endPoint.x, line.endPoint.y);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("startPoint", startPoint)
                    .append("endPoint", endPoint)
                    .toString();
        }
    }

    protected class Point
    {
        double x;
        double y;

        Point(double x, double y)
        {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("x", x)
                    .append("y", y)
                    .toString();
        }
    }

    private LinkedHashSet<Location> locationSet;

    private double xMin = Integer.MAX_VALUE;
    private double xMax = Integer.MIN_VALUE;
    private double yMin = Integer.MAX_VALUE;
    private double yMax = Integer.MIN_VALUE;

    public Polygon(UUID uuid, LinkedHashSet<Location> locationSet)
    {
        this(uuid, locationSet, false);
    }

    public Polygon(UUID uuid, LinkedHashSet<Location> locationSet, boolean ignoreHeight)
    {
        super(ignoreHeight);

        this.uuid = uuid;
        this.locationSet = locationSet;

        if (locationSet.size() > 0)
        {
            Location firstLocation = ((Location) locationSet.toArray()[0]);
            this.world = firstLocation != null ? firstLocation.getWorld() : null;
        }

        this.calcBorders();
    }

    private void calcBorders()
    {
        // Calculates borders of polygon, so we can later reduce the time inRegion takes for
        // players who are not nearby
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
        // Check if location is in the same world
        if (!location.getWorld().getName().equals(this.world.getName()))
        {
            return false;
        }

        // To reduce complexity we ignore height for polygons
        // Since precision is not that important here I'll use ray casting
        double x = location.getX();
        double y = location.getY();

        // Quick test first
        if (x < this.xMin || x > this.xMax || y < this.yMin || y > this.yMax)
        {
            // Not even gonna bother making a ray cast
            return false;
        }

        // Create point outside polygon
        double xStart = this.xMin - 1;
        double yStart = this.yMin - 1;

        Line rayLine = new Line(location, new Location(null, xStart, yStart, 0D));
        int intersections = 0;

        // Get last location to create a line between the first and last location
        Location previousLocation = (Location) this.locationSet.toArray()[this.locationSet.size() - 1];

        // Loop through all locations to create lines
        for (Location itLocation : this.locationSet)
        {
            if (previousLocation != null)
            {
                // Check intersect
                Line line = new Line(previousLocation, itLocation);

                // If the lines intersect add one to 'intersections'
                if (line.intersect(rayLine))
                {
                    intersections++;
                }
            }

            previousLocation = itLocation;
        }

        // If the location is in the polygon the number of intersections will be odd
        return intersections % 2 == 1;
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

        if (!(object instanceof Polygon))
        {
            return false;
        }

        Polygon polygon = (Polygon) object;

        return new EqualsBuilder()
                .append(this.uuid, polygon.uuid)
                .append(this.locationSet, polygon.locationSet)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.uuid, this.locationSet);
    }
}
