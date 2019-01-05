package nl.tim.questplugin.storage.workers;

import com.google.inject.Inject;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.storage.Storage;

import javax.inject.Singleton;
import java.util.ArrayList;
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
        QuestPlugin.getLog().warning("SQL is not yet implemented!");
        return false;
    }

    @Override
    public void save(UUID uuid, DataType dataType, DataPair dataPair)
    {
        ArrayList<DataPair> dataPairs = new ArrayList<>();

        dataPairs.add(dataPair);
        this.save(uuid, dataType, dataPairs);
    }

    @Override
    public void save(UUID uuid, DataType dataType, List<DataPair> dataPairs)
    {
        System.out.println("SQL:" + uuid);
        System.out.println("SQL:" + dataType);
        System.out.println("SQL:" + dataPairs);
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
