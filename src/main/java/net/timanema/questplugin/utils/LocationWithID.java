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

package net.timanema.questplugin.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class LocationWithID extends Location
{
    private UUID uuid;

    public LocationWithID(UUID uuid, World world, double x, double y, double z)
    {
        super(world, x, y, z);

        this.uuid = uuid;
    }

    public LocationWithID(UUID uuid, World world, double x, double y, double z,float yaw, float pitch)
    {
        super(world, x, y, z, yaw, pitch);

        this.uuid = uuid;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (object == null || getClass() != object.getClass())
        {
            return false;
        }

        LocationWithID that = (LocationWithID) object;

        return new EqualsBuilder()
                .appendSuper(super.equals(object))
                .append(uuid, that.uuid)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(uuid)
                .toHashCode();
    }
}
