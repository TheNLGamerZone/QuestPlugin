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
import org.bukkit.Location;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.UUID;

public class Sphere extends Region
{
    private LocationWithID center;
    private double radius;

    public Sphere(UUID uuid, LocationWithID center, double radius)
    {
        this(uuid, center, radius, false);
    }

    public Sphere(UUID uuid, LocationWithID center, double radius, boolean ignoreHeight)
    {
        super(ID_SPHERE, uuid, center.getWorld(), ignoreHeight);

        this.center = center;
        this.radius = radius;
    }

    public double getRadius()
    {
        return this.radius;
    }

    public void setRadius(double radius)
    {
        this.radius = radius;
    }

    public void setCenter(LocationWithID location)
    {
        this.center = location;
    }

    @Override
    public boolean inRegion(Location location, boolean ignoreHeight)
    {
        // Check if location is in the same world
        if (!location.getWorld().getName().equals(this.getWorld().getName()))
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
                .append(this.getUUID(), sphere.getUUID())
                .append(this.center, sphere.center)
                .append(this.radius, sphere.radius)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.getUUID(), this.center, this.radius);
    }

    @Override
    public LinkedHashSet<LocationWithID> getLocations()
    {
        LinkedHashSet<LocationWithID> result = new LinkedHashSet<>();

        result.add(this.center);

        return result;
    }
}
