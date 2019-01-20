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

import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedHashSet;
import java.util.UUID;

public abstract class Region
{
    // Constants
    public static final String ID_CUBE = "911527db";
    public static final String ID_SPHERE = "2ad05917";
    public static final String ID_POLYGON = "0d403ffa";

    // Regular fields
    private String regionFileIdentifier;
    private UUID uuid;
    private World world;
    private boolean ignoreHeight;

    public Region(String regionFileIdentifier, UUID uiud, World world, boolean ignoreHeight)
    {
        this.regionFileIdentifier = regionFileIdentifier;
        this.uuid = uiud;
        this.world = world;
        this.ignoreHeight = ignoreHeight;
    }

    public boolean inRegion(Location location)
    {
        return inRegion(location, ignoreHeight);
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public World getWorld()
    {
        return this.world;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public String getRegionFileIdentifier()
    {
        return this.regionFileIdentifier;
    }

    public boolean heightIgnored()
    {
        return this.ignoreHeight;
    }

    public abstract boolean inRegion(Location location, boolean ignoreHeight);

    public abstract boolean equals(Object object);

    public abstract int hashCode();

    public abstract LinkedHashSet<Location> getLocations();
}
