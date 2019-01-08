package nl.tim.questplugin.storage.image.builders;

import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.quest.stage.Stage;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.StorageProvider;
import nl.tim.questplugin.storage.image.ImageBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class StageImageBuilder implements ImageBuilder<Stage>
{
    private QuestPlugin questPlugin;
    private Storage storage;

    @Inject
    public StageImageBuilder(QuestPlugin questPlugin, StorageProvider storageProvider)
    {
        this.questPlugin = questPlugin;
        this.storage = storageProvider.getStorage(QuestPlugin.storageType);
    }

    @Override
    public void save(Stage stage)
    {

    }

    @Override
    public Stage load(UUID uuid)
    {
        return null;
    }
}
