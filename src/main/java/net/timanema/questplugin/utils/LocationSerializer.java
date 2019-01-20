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

import net.timanema.questplugin.storage.Storage;
import net.timanema.questplugin.QuestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.UUID;

@Singleton
public final class LocationSerializer
{
    /**
     * Creates a {@link HashSet} containing {@link Storage.DataPair}s which can be saved
     * from a given {@link Location}.
     * @param location Location to serialize
     * @return Returns {@link HashSet} containing {@link Storage.DataPair}s representing
     * the given {@link Location}.
     */
    public static LinkedHashSet<Storage.DataPair<String>> serializeLocation(Location location)
    {
        LinkedHashSet<Storage.DataPair<String>> result = new LinkedHashSet<>();

        // Adding data
        result.add(new Storage.DataPair<>("world-uuid", location.getWorld().getUID().toString()));
        result.add(new Storage.DataPair<>("x", "" + location.getX()));
        result.add(new Storage.DataPair<>("y", "" + location.getY()));
        result.add(new Storage.DataPair<>("z", "" + location.getZ()));

        return result;
    }

    /**
     * Creates a {@link Location} based on given {@link Storage.DataPair}s.
     * @param data {@link Storage.DataPair}s used to build location
     * @return {@link Location} based on given {@link Storage.DataPair}s.
     */
    public static Location deserializeLocation(HashSet<Storage.DataPair<String>> data)
    {
        String[] location = new String[4];

        if (data.size() != 4)
        {
            QuestPlugin.getLog().warning("Invalid amount of location data supplied: '" + data.size() + "' (4 expected).");
            return null;
        }

        for (Storage.DataPair<String> dataPair : data)
        {
            String key = dataPair.getKey();
            int index = key.contains("world-uuid")  ? 0 :
                        key.contains("x")           ? 1 :
                        key.contains("y")           ? 2 :
                        key.contains("z")           ? 3 :
                                -1;

            if (index != -1)
            {
                location[index] = dataPair.getData();
            } else
            {
                QuestPlugin.getLog().warning("Invalid location key: '" + key + "' supplied.");
                return null;
            }
        }

        return new Location(
                Bukkit.getWorld(UUID.fromString(location[0])),
                Double.valueOf(location[1]),
                Double.valueOf(location[2]),
                Double.valueOf(location[3])
        );
    }
}
