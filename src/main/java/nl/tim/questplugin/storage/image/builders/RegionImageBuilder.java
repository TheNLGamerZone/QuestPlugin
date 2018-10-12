package nl.tim.questplugin.storage.image.builders;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.area.Region;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.image.ImageBuilder;

import java.util.UUID;

@Singleton
public class RegionImageBuilder implements ImageBuilder<Region>
{
    private Storage storage;

    @Inject
    public RegionImageBuilder(Storage storage)
    {
        this.storage = storage;
    }

    @Override
    public void save(Region region) {

    }

    @Override
    public Region load(UUID uuid) {
        return null;
    }
}
