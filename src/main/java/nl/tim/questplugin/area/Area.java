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

package nl.tim.questplugin.area;

import nl.tim.questplugin.player.QPlayer;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Area
{
    private UUID uuid;
    private Set<Region> regions;

    public Area(UUID uuid, Set<Region> regions)
    {
        this.uuid = uuid;
        this.regions = regions;
    }

    public boolean inArea(QPlayer player)
    {
        // Loop through all regions linked to this area
        for (Region region : regions)
        {
            // Check if the player is in this regio
            if (region.inRegion(player.getLastLocation()))
            {
                return true;
            }
        }

        return false;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public Set<Region> getRegions()
    {
        return this.regions;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.uuid, this.regions);
    }
}
