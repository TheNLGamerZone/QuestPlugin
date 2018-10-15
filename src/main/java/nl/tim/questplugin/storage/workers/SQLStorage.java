package nl.tim.questplugin.storage.workers;

import com.google.inject.Inject;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.storage.Storage;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Singleton
public class SQLStorage implements Storage
{
    private QuestPlugin questPlugin;

    @Inject
    public SQLStorage(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
    }

    @Override
    public boolean init()
    {
        QuestPlugin.logger.warning("SQL is not yet implemented!");
        return false;
    }

    @Override
    public void save(UUID uuid, DataType dataType, DataPair dataPair)
    {
        this.save(uuid, dataType, new DataPair[]{dataPair});
    }

    @Override
    public void save(UUID uuid, DataType dataType, DataPair[] dataPairs)
    {
        System.out.println("SQL:" + uuid);
        System.out.println("SQL:" + dataType);
        System.out.println("SQL:" + Arrays.toString(dataPairs));
    }

    @Override
    public DataPair load(UUID uuid, DataType dataType, String key)
    {
        return null;
    }

    @Override
    public List<DataPair> load(UUID uuid, DataType dataType)
    {
        return null;
    }

    @Override
    public List<UUID> getSavedObjectsUID(DataType dataType)
    {
        return null;
    }
}
