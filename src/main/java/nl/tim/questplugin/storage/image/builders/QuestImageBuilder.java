package nl.tim.questplugin.storage.image.builders;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.quest.Quest;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.image.ImageBuilder;

import java.util.UUID;

@Singleton
public class QuestImageBuilder implements ImageBuilder<Quest>
{
    private Storage storage;

    @Inject
    public QuestImageBuilder(Storage storage)
    {
        this.storage = storage;
    }

    @Override
    public void save(Quest quest) {

    }

    @Override
    public Quest load(UUID uuid) {
        return null;
    }
}
