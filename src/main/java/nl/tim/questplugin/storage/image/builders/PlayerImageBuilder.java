package nl.tim.questplugin.storage.image.builders;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.StorageProvider;
import nl.tim.questplugin.storage.image.ImageBuilder;

import java.util.UUID;

@Singleton
public class PlayerImageBuilder implements ImageBuilder<QPlayer>
{
    private Storage storage;

    @Inject
    public PlayerImageBuilder(StorageProvider storageProvider)
    {
        this.storage = storageProvider.getStorage(QuestPlugin.storageType);
    }

    @Override
    public void save(QPlayer qPlayer) {

    }

    @Override
    public QPlayer load(UUID uuid) {
        return null;
    }
}
