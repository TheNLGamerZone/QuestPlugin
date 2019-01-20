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
import net.timanema.questplugin.storage.Storage;
import net.timanema.questplugin.storage.StorageProvider;
import net.timanema.questplugin.area.Area;
import net.timanema.questplugin.area.Region;
import net.timanema.questplugin.storage.image.ImageBuilder;

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
            <region_uuid>: REGION
            <region_uuid>: REGION
            .
            .
            .
        .
        .
        .
         */

        List<Storage.DataPair<String>> dataPairs = new ArrayList<>();

        if (area.getRegions() == null)
        {
            QuestPlugin.getLog().warning("Area with ID '" + area.getUUID() + "' does not contain regions, will not be saved");
            return;
        }

        // Save regions
        for (Region region : area.getRegions())
        {
            dataPairs.add(new Storage.DataPair<>(region.getUUID().toString(), "REGION"));
            this.questPlugin.getRegionImageBuilder().save(region);
        }

        this.storage.save(area.getUUID(), Storage.DataType.AREA, dataPairs);
    }

    @Override
    public Area load(UUID uuid)
    {
        List<Storage.DataPair<String>> dataPairs = this.storage.load(uuid, Storage.DataType.AREA);
        Set<Region> regions = new HashSet<>();

        // Check if the UUID was valid
        if (dataPairs == null || dataPairs.isEmpty())
        {
            QuestPlugin.getLog().warning("Area with ID '" + uuid + "' failed to load: not found");
            return null;
        }

        // Load regions
        for (Storage.DataPair dataPair : dataPairs)
        {
            // Load region
            Region region = this.questPlugin.getRegionImageBuilder().load(UUID.fromString(dataPair.getKey()));

            // Check if region failed to load properly
            if (region == null)
            {
                QuestPlugin.getLog().warning("Area with ID '" + uuid + "' could not load, because region '" + dataPair.getKey() + "' failed to load");
                return null;
            }

            // Add region to set
            regions.add(region);
        }

        return new Area(uuid, regions);
    }
}
