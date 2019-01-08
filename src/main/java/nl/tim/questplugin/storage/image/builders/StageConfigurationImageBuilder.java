package nl.tim.questplugin.storage.image.builders;

import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.quest.stage.StageConfiguration;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.StorageProvider;
import nl.tim.questplugin.storage.image.ImageBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class StageConfigurationImageBuilder implements ImageBuilder<StageConfiguration>
{
    private QuestPlugin questPlugin;
    private Storage storage;

    @Inject
    public StageConfigurationImageBuilder(QuestPlugin questPlugin, StorageProvider storageProvider)
    {
        this.questPlugin = questPlugin;
        this.storage = storageProvider.getStorage(QuestPlugin.storageType);
    }

    @Override
    public void save(StageConfiguration configuration)
    {

    }

    @Override
    public StageConfiguration load(UUID uuid)
    {
        return null;
    }
}
