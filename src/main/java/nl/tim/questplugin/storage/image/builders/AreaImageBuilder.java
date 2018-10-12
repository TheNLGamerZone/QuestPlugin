package nl.tim.questplugin.storage.image.builders;

import nl.tim.questplugin.area.Area;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.image.ImageBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class AreaImageBuilder implements ImageBuilder<Area>
{
    private Storage storage;

    @Inject
    public AreaImageBuilder(Storage storage)
    {
        this.storage = storage;
    }

    @Override
    public void save(Area area)
    {
        Storage.DataPair<String, Double> dataPair = new Storage.DataPair<>("loc.x", 42.1);

        storage.save(null, Storage.DataType.AREA, dataPair);
    }

    @Override
    public Area load(UUID uuid)
    {
        return null;
    }
}
