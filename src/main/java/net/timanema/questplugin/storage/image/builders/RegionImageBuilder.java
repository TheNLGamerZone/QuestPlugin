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

package net.timanema.questplugin.storage.image.builders;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.timanema.questplugin.QuestPlugin;
import net.timanema.questplugin.storage.Storage;
import net.timanema.questplugin.storage.StorageProvider;
import net.timanema.questplugin.area.Cube;
import net.timanema.questplugin.area.Polygon;
import net.timanema.questplugin.area.Region;
import net.timanema.questplugin.area.Sphere;
import net.timanema.questplugin.storage.image.ImageBuilder;
import net.timanema.questplugin.utils.LocationWithID;
import net.timanema.questplugin.utils.StringUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

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
        if (region == null)
        {
            return;
        }

        /*
        Regions will be saved in the following format:
        <uuid>:
            type: <type>
            ignore-z: <boolean>
            radius: <radius - -1 if region is not a sphere>
            locations:
                - <location_uuid>
                - <location_uuid>
         */

        List<Storage.DataPair> result = new ArrayList<>(region.getLocations().size() + 3);

        result.add(new Storage.DataPair<>("type", region.getRegionFileIdentifier()));
        result.add(new Storage.DataPair<>("ignore-z", region.heightIgnored() + ""));
        result.add(new Storage.DataPair<>("radius", region instanceof Sphere ? "" + ((Sphere) region).getRadius() : "-1"));
        result.add(this.saveLocations(region));

        // Save data pairs
        this.storage.save(region.getUUID(), Storage.DataType.REGION, result);
    }

    private Storage.DataPair<Collection> saveLocations(Region region)
    {
        Set<String> ids = new HashSet<>();

        for (LocationWithID loc : region.getLocations())
        {
            ids.add(loc.getUUID().toString());
            this.questPlugin.getLocationImageBuilder().save(loc);
        }

        return new Storage.DataPair<>("locations", ids);
    }

    @Override
    public Region load(UUID uuid) {
        List<Storage.DataPair> dataPairs = this.storage.load(uuid, Storage.DataType.REGION);

        // Check if the data pairs could be loaded and the uuid was valid
        if (dataPairs == null || dataPairs.size() == 0)
        {
            return null;
        }

        String type = null;
        boolean ignoreHeight = false;
        double radius = -1;
        Storage.DataPair<Collection> locationPair = null;

        for (Storage.DataPair dataPair : dataPairs)
        {
            String key  = dataPair.getKey();

            // Check if this is a collection
            if (dataPair.isCollection())
            {
                Storage.DataPair<Collection> collectionPair = new Storage.DataPair<>(key,
                        (Collection) dataPair.getData());

                if (!key.equals("locations"))
                {
                    QuestPlugin.getLog().warning("Unknown key-value pair supplied when constructing region: '" +
                            dataPair.getKey() + ": " + dataPair.getData() + "'");
                    return null;
                }

                locationPair = collectionPair;
            } else
            {
                Storage.DataPair<String> stringPair = new Storage.DataPair<>(key, (String) dataPair.getData());
                String data = stringPair.getData();

                switch (key)
                {
                    case "type":
                        type = data;
                        break;
                    case "ignore-z":
                        if (BooleanUtils.toBooleanObject(data) != null)
                        {
                            ignoreHeight = BooleanUtils.toBoolean(data);
                        }
                        break;
                    case "radius":
                        if (NumberUtils.isNumber(data))
                        {
                            radius = Double.parseDouble(data);
                        }
                        break;
                    default:
                        QuestPlugin.getLog().warning("Unknown key-value pair supplied when constructing region: '" +
                                dataPair.getKey() + ": " + dataPair.getData() + "'");
                        return null;
                }
            }
        }

        // Load locations
        LinkedHashSet<LocationWithID> locations = this.loadLocations(locationPair);

        // Check if a value was not found
        if (type == null || locationPair == null || locations == null || locations.size() == 0)
        {
            QuestPlugin.getLog().warning("Invalid region data supplied for region with ID '" + uuid + "'");
            return null;
        }

        // Create region
        Region region = this.createRegion(uuid, type, ignoreHeight, radius, locations);

        // Check if region could not be loaded for some reason
        if (region == null)
        {
            QuestPlugin.getLog().warning("Region '" + uuid + "' could not be loaded!");
        }

        return region;
    }

    private Region createRegion(UUID uuid, String type, boolean ignoreHeight, double radius, LinkedHashSet<LocationWithID> locations)
    {
        Region result;

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

                Iterator<LocationWithID> iter = locations.iterator();

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

                result = new Sphere(uuid, locations.iterator().next(), radius, ignoreHeight);
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

                result = new Polygon(uuid, locations, ignoreHeight);
                break;
            default:
                QuestPlugin.getLog().warning("Invalid region type supplied: " + type);
                return null;
        }

        return result;
    }

    private LinkedHashSet<LocationWithID> loadLocations(Storage.DataPair<Collection> dataPair)
    {
        LinkedHashSet<LocationWithID> locations = new LinkedHashSet<>();

        if (dataPair == null)
        {
            return locations;
        }

        for (Object rawID : dataPair.getData())
        {
            if (StringUtils.isUUID(rawID))
            {
                LocationWithID loc = this.questPlugin.getLocationImageBuilder().load(UUID.fromString(rawID.toString()));

                if (loc == null)
                {
                    QuestPlugin.getLog().warning("Location " + rawID + " failed to load, this might impact how the region will behave!");
                    continue;
                }

                locations.add(loc);
            }
        }

        return locations;
    }
}
