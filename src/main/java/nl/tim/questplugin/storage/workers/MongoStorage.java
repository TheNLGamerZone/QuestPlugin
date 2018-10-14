package nl.tim.questplugin.storage.workers;

import com.google.inject.Inject;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.storage.Storage;

import java.util.UUID;

public class MongoStorage implements Storage
{
    private QuestPlugin questPlugin;

    @Inject
    public MongoStorage(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
    }

    @Override
    public boolean init() {
        QuestPlugin.logger.warning("Mongo is not yet implemented!");
        return false;
    }

    @Override
    public void save(UUID uuid, DataType dataType, DataPair dataPair) {

    }

    @Override
    public void save(UUID uuid, DataType dataType, DataPair[] dataPairs) {

    }

    @Override
    public DataPair load(UUID uuid, DataType dataType, String key) {
        return null;
    }

    @Override
    public DataPair[] load(UUID uuid, DataType dataType) {
        return new DataPair[0];
    }

    @Override
    public UUID[] getSavedObjectsUID(DataType dataType) {
        return new UUID[0];
    }
}
