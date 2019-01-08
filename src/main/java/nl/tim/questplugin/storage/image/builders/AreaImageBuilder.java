package nl.tim.questplugin.storage.image.builders;

import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.area.Area;
import nl.tim.questplugin.area.Region;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.StorageProvider;
import nl.tim.questplugin.storage.image.ImageBuilder;

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

        List<Storage.DataPair> dataPairs = new ArrayList<>();

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
        List<Storage.DataPair> dataPairs = this.storage.load(uuid, Storage.DataType.AREA);
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
