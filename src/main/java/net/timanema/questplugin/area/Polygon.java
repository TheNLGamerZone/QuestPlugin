/*
 * Copyright (C) 2019  Tim Anema
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.timanema.questplugin.area;

import net.timanema.questplugin.utils.LocationWithID;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Location;

import java.awt.geom.Line2D;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.UUID;

public class Polygon extends Region
{
    private LinkedHashSet<LocationWithID> locationSet;

    private double xMin = Integer.MAX_VALUE;
    private double xMax = Integer.MIN_VALUE;
    private double yMin = Integer.MAX_VALUE;
    private double yMax = Integer.MIN_VALUE;

    protected class Line
    {
        private Point startPoint;
        private Point endPoint;

        Line(Location startLocation, Location endLocation)
        {
            this.startPoint = new Point(startLocation.getX(), startLocation.getY());
            this.endPoint = new Point(endLocation.getX(), endLocation.getY());
        }

        private boolean intersect(Line line)
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
        private double x;
        private double y;

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

    public Polygon(UUID uuid, LinkedHashSet<LocationWithID> locationSet)
    {
        this(uuid, locationSet, false);
    }

    public Polygon(UUID uuid, LinkedHashSet<LocationWithID> locationSet, boolean ignoreHeight)
    {
        super(ID_POLYGON, uuid, null, ignoreHeight);

        this.locationSet = locationSet;

        if (locationSet.size() > 0)
        {
            Location firstLocation = ((Location) locationSet.toArray()[0]);
            this.setWorld(firstLocation != null ? firstLocation.getWorld() : null);
        }

        this.calcBorders();
    }

    public boolean updateLocation(Location oldLocation, Location newLocation)
    {
        for (Location location : this.locationSet)
        {
            if (location.equals(oldLocation))
            {
                location.setWorld(newLocation.getWorld());
                location.setX(newLocation.getX());
                location.setY(newLocation.getY());
                location.setZ(newLocation.getZ());

                return true;
            }
        }

        return false;
    }

    private void calcBorders()
    {
        // Calculates borders of polygon, so we can later reduce the time inRegion takes for
        // players who are not nearby
        for (Location location : this.locationSet)
        {
            double x = location.getX();
            double y = location.getY();

            if (x < this.xMin)
            {
                this.xMin = x;
            } else if (x > this.xMax)
            {
                this.xMax = x;
            }

            if (y < this.yMin)
            {
                this.yMin = y;
            } else if (y > this.yMax)
            {
                this.yMax = y;
            }
        }
    }

    @Override
    public boolean inRegion(Location location, boolean ignoreHeight)
    {
        // Check if location is in the same world
        if (!location.getWorld().getName().equals(this.getWorld().getName()))
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
                // Create line between current points
                Line line = new Line(previousLocation, itLocation);

                // If the lines intersect increment 'intersections'
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
                .append(this.getUUID(), polygon.getUUID())
                .append(this.locationSet, polygon.locationSet)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.getUUID(), this.locationSet);
    }

    @Override
    public LinkedHashSet<LocationWithID> getLocations()
    {
        return this.locationSet;
    }
}
