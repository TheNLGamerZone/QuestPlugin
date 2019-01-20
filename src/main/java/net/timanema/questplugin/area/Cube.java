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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.bukkit.Location;

import java.util.LinkedHashSet;
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
        super(ID_CUBE, uuid, location1.getWorld(), ignoreHeight);

        this.location1 = location1;
        this.location2 = location2;
    }

    @Override
    public boolean inRegion(Location location, boolean ignoreHeight)
    {
        // Check if location is in the same world
        if (!location.getWorld().getName().equals(this.getWorld().getName()))
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

    public void setFirstLocation(Location location)
    {
        this.location1 = location;
    }

    public void setSecondLocation(Location location)
    {
        this.location2 = location;
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
                .append(this.getUUID(), cube.getUUID())
                .append(this.location1, cube.location1)
                .append(this.location2, cube.location2)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.getUUID(), this.location1, this.location2);
    }

    @Override
    public LinkedHashSet<Location> getLocations()
    {
        LinkedHashSet<Location> result = new LinkedHashSet<>();

        result.add(this.location1);
        result.add(this.location2);

        return result;
    }
}
