package nl.tim.questplugin.storage.image.builders;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.area.Region;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.StorageProvider;
import nl.tim.questplugin.storage.image.ImageBuilder;

import java.util.UUID;

@Singleton
public class RegionImageBuilder implements ImageBuilder<Region>
{
    private Storage storage;

    @Inject
    public RegionImageBuilder(StorageProvider storageProvider)
    {
        this.storage = storageProvider.getStorage(QuestPlugin.storageType);
    }

    @Override
    public void save(Region region) {

    }

    @Override
    public Region load(UUID uuid) {
        return null;
    }
}
