package nl.tim.questplugin.storage.image.builders;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.quest.Quest;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.StorageProvider;
import nl.tim.questplugin.storage.image.ImageBuilder;

import java.util.UUID;

@Singleton
public class QuestImageBuilder implements ImageBuilder<Quest>
{
    private QuestPlugin questPlugin;
    private Storage storage;

    @Inject
    public QuestImageBuilder(QuestPlugin questPlugin, StorageProvider storageProvider)
    {
        this.questPlugin = questPlugin;
        this.storage = storageProvider.getStorage(QuestPlugin.storageType);
    }

    @Override
    public void save(Quest quest) {

    }

    @Override
    public Quest load(UUID uuid) {
        return null;
    }
}
