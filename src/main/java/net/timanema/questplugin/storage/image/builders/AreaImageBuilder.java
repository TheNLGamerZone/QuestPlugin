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

import net.timanema.questplugin.QuestPlugin;
import net.timanema.questplugin.quest.Quest;
import net.timanema.questplugin.storage.Storage;
import net.timanema.questplugin.storage.StorageProvider;
import net.timanema.questplugin.area.Area;
import net.timanema.questplugin.area.Region;
import net.timanema.questplugin.storage.image.ImageBuilder;
import net.timanema.questplugin.utils.LocationWithID;
import net.timanema.questplugin.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class AreaImageBuilder implements ImageBuilder<Area>
{
    private QuestPlugin questPlugin;
    private Storage storage;

    @Inject
    public AreaImageBuilder(QuestPlugin questPlugin, StorageProvider storageProvider)
    {
        this.questPlugin = questPlugin;
        this.storage = storageProvider.getStorage(QuestPlugin.storageType);
    }

    @Override
    public void save(Area area)
    {
        if (area == null)
        {
            return;
        }
        /*
        Areas will be saved in the following format:
        <uuid>:
            regions:
                - <region_uuid>
                - <region_uuid>
         */

        if (area.getRegions() == null)
        {
            QuestPlugin.getLog().warning("Area with ID '" + area.getUUID() + "' does not contain regions, will not be saved");
            return;
        }

        Set<String> ids = new HashSet<>();

        // Save regions
        for (Region region : area.getRegions())
        {
            ids.add(region.getUUID().toString());
            this.questPlugin.getRegionImageBuilder().save(region);
        }

        List<Storage.DataPair> dataPairs = new ArrayList<>(
                Collections.singletonList(new Storage.DataPair<>("regions", ids)));

        this.storage.save(area.getUUID(), Storage.DataType.AREA, dataPairs);
    }

    @Override
    public Area load(UUID uuid)
    {
        List<Storage.DataPair> dataPairs = this.storage.load(uuid, Storage.DataType.AREA);
        Set<Region> regions = null;

        // Check if the UUID was valid
        if (dataPairs == null || dataPairs.isEmpty())
        {
            QuestPlugin.getLog().warning("Area with ID '" + uuid + "' failed to load: not found");
            return null;
        }

        // Load regions
        for (Storage.DataPair dataPair : dataPairs)
        {
            String key = dataPair.getKey();

            if (dataPair.isCollection())
            {
                Storage.DataPair<Collection> collectionPair = new Storage.DataPair<>(key,
                        (Collection) dataPair.getData());

                // Check if this is a valid data pair
                if (!key.equals("regions"))
                {
                    QuestPlugin.getLog().warning("Unknown data type received when constructing area: " + key);
                    continue;
                }

                regions = this.loadRegions(collectionPair);
            }
        }

        // Check if regions weren't loaded
        if (regions == null)
        {
            QuestPlugin.getLog().warning("Could not find regions for area " + uuid);
            return null;
        }

        return new Area(uuid, regions);
    }

    private Set<Region> loadRegions(Storage.DataPair<Collection> dataPair)
    {
        Set<Region> regions = new HashSet<>();

        if (dataPair == null)
        {
            return regions;
        }

        for (Object rawID : dataPair.getData())
        {
            if (StringUtils.isUUID(rawID))
            {
                // Load region
                Region region = this.questPlugin.getRegionImageBuilder().load(UUID.fromString(rawID.toString()));

                // Check if region failed to load properly
                if (region == null)
                {
                    QuestPlugin.getLog().warning("Area with ID '" + rawID + "' could not load, because region '" + dataPair.getKey() + "' failed to load");
                    return null;
                }

                // Add region to set
                regions.add(region);
            }
        }

        return regions;
    }
}
