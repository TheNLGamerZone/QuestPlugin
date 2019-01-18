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

package nl.tim.questplugin.storage.image.builders;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.area.Cube;
import nl.tim.questplugin.area.Polygon;
import nl.tim.questplugin.area.Region;
import nl.tim.questplugin.area.Sphere;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.StorageProvider;
import nl.tim.questplugin.storage.image.ImageBuilder;
import nl.tim.questplugin.utils.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

@Singleton
public class RegionImageBuilder implements ImageBuilder<Region>
{
    private QuestPlugin questPlugin;
    private Storage storage;

    @Inject
    public RegionImageBuilder(QuestPlugin questPlugin, StorageProvider storageProvider)
    {
        this.questPlugin = questPlugin;
        this.storage = storageProvider.getStorage(QuestPlugin.storageType);
    }

    @Override
    public void save(Region region)
    {
        /*
        Regions will be saved in the following format:
        <uuid>:
            type: <type>
            ignore-z: <boolean>
            radius: <radius - -1 if region is not a sphere>
            locations:
                <location_hash>:
                    world-uuid: <world-uuid>
                    x: <x>
                    y: <y>
                    z: <z>
                 .
                 .
                 .
        .
        .
        .
         */

        List<Storage.DataPair<String>> locationDataPairs = new ArrayList<>();
        UUID uuid = region.getUUID();

        for (Location location : region.getLocations())
        {
            String locationHash = "" + location.hashCode();

            for (Storage.DataPair<String> locationData : LocationSerializer.serializeLocation(location))
            {
                locationData.prependKey("locations." + locationHash + ".");
                locationDataPairs.add(locationData);
            }
        }

        List<Storage.DataPair<String>> dataPairs = new ArrayList<>();

        // Add data
        dataPairs.add(new Storage.DataPair<>("type", region.getRegionFileIdentifier()));
        dataPairs.add(new Storage.DataPair<>("ignore-z", region.heightIgnored() + ""));
        dataPairs.add(new Storage.DataPair<>("radius",
                region instanceof Sphere ? "" + ((Sphere) region).getRadius() : "-1"));
        dataPairs.addAll(locationDataPairs);

        // Save data pairs
        this.storage.save(uuid, Storage.DataType.REGION, dataPairs);
    }

    private LinkedHashMap<String, Location> loadLocations(List<Storage.DataPair<String>> dataPairs)
    {
        LinkedHashMap<String, Location> locations = new LinkedHashMap<>();

        // Load data
        for (Storage.DataPair dataPair : dataPairs)
        {
            if (dataPair.getKey().contains("locations"))
            {
                String strippedKey = dataPair.getKey().substring(dataPair.getKey().indexOf("locations") + "locations.".length());
                String hashCode = strippedKey.split(".")[0];
                Location loc = locations.getOrDefault(hashCode, new Location(null, 0, 0, 0));

                switch (strippedKey.split(".")[1])
                {
                    case "world-uuid":
                        loc.setWorld(Bukkit.getWorld(UUID.fromString(dataPair.getData().toString())));
                        break;
                    case "x":
                        loc.setX(Double.valueOf(dataPair.getData().toString()));
                        break;
                    case "y":
                        loc.setY(Double.valueOf(dataPair.getData().toString()));
                        break;
                    case "z":
                        loc.setZ(Double.valueOf(dataPair.getData().toString()));
                        break;
                    default:
                        QuestPlugin.getLog().warning("Unknown location key supplied when constructing region: " +
                                dataPair.getKey());
                        return null;
                }

                // Update hash map
                locations.put(hashCode, loc);
            } else
            {
                QuestPlugin.getLog().warning("Unknown key-value pair supplied when constructing region: '" +
                        dataPair.getKey() + ": " + dataPair.getData() + "'");
                return null;
            }
        }

        return locations;
    }


    @Override
    public Region load(UUID uuid) {
        Region result = null;
        List<Storage.DataPair<String>> dataPairs = this.storage.load(uuid, Storage.DataType.REGION);

        // Check if the data pairs could be loaded and the uuid was valid
        if (dataPairs == null || dataPairs.size() == 0)
        {
            return null;
        }

        String type = null;
        boolean ignoreHeight = false;
        double radius = -1;
        LinkedHashMap<String, Location> locations = loadLocations(dataPairs);

        // Load data
        for (Storage.DataPair dataPair : dataPairs)
        {
            if (dataPair.getKey().contains("type"))
            {
                type = dataPair.getData().toString();
            } else if (dataPair.getKey().contains("ignore-z"))
            {
                ignoreHeight = Boolean.valueOf(dataPair.getData().toString());
            } else if (dataPair.getKey().contains("radius"))
            {
                radius = Double.valueOf(dataPair.getData().toString());
            } else if (!dataPair.getKey().contains("locations"))
            {
                QuestPlugin.getLog().warning("Unknown key-value pair supplied when constructing region: '" +
                        dataPair.getKey() + ": " + dataPair.getData() + "'");
                return null;
            }
        }

        // Check if all values where found
        if (type == null || locations == null ||  locations.size() < 1)
        {
            QuestPlugin.getLog().warning("Invalid region data supplied for region with ID '" + uuid + "'");
            return null;
        }

        // Create region
        switch (type)
        {
            case Region.ID_CUBE:
                // Check if required info was fetched
                if (locations.size() != 2)
                {
                    QuestPlugin.getLog().warning("Invalid amount (" + locations.size() + " supplied, 2 expected) " +
                            "of locations supplied for type 'CUBE'");
                    return null;
                }

                Iterator<Location> iter = locations.values().iterator();

                result = new Cube(uuid, iter.next(), iter.next(), ignoreHeight);
                break;
            case Region.ID_SPHERE:
                // Check if required info was fetched
                if (locations.size() != 1 || radius <= 0)
                {
                    QuestPlugin.getLog().warning(
                            "Invalid " + (locations.size() != 1 ? "amount of locations" : "radius") +
                                    " supplied for type 'SPHERE'");
                    return null;
                }
                break;
            case Region.ID_POLYGON:
                // Check if required info was fetched
                if (locations.size() < 3)
                {
                    QuestPlugin.getLog().warning(
                            "Invalid amount (" + locations.size() + " supplied, at least 3 expected) " +
                                    "of locations supplied for type 'POLYGON'");
                    return null;
                }

                result = new Polygon(uuid, new LinkedHashSet<>(locations.values()), ignoreHeight);
                break;
            default:
                QuestPlugin.getLog().warning("Invalid region type supplied: " + type);
                return null;
        }

        return result;
    }
}
