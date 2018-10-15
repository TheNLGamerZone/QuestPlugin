package nl.tim.questplugin.storage.workers;

import com.google.inject.Inject;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.storage.Storage;

import java.util.List;
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
    public List<DataPair> load(UUID uuid, DataType dataType) {
        return null;
    }

    @Override
    public List<UUID> getSavedObjectsUID(DataType dataType) {
        return null;
    }
}
