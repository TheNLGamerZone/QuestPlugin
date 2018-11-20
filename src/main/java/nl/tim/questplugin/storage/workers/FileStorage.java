package nl.tim.questplugin.storage.workers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.storage.ConfigHandler;
import nl.tim.questplugin.storage.Storage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Singleton
public class FileStorage implements Storage
{
    private QuestPlugin questPlugin;
    private File storageLocation;

    @Inject
    public FileStorage(QuestPlugin questPlugin, @Named("config") File storageLocation)
    {
        this.questPlugin = questPlugin;
        this.storageLocation = storageLocation;
    }

    @Override
    public boolean init()
    {
        QuestPlugin.logger.info("Checking files");

        // Create all files
        File areaFile = new File(storageLocation + File.separator +
                DataType.AREA.getFilePath().replace("/", File.separator));
        File playerFile = new File(storageLocation + File.separator +
                DataType.PLAYER.getFilePath().replace("/", File.separator));
        File questFile = new File(storageLocation + File.separator +
                DataType.QUEST.getFilePath().replace("/", File.separator));
        File regionFile = new File(storageLocation + File.separator +
                DataType.REGION.getFilePath().replace("/", File.separator));

        // Check if all files exist
        ConfigHandler.createFileIfNotExists(areaFile);
        ConfigHandler.createFileIfNotExists(playerFile);
        ConfigHandler.createFileIfNotExists(questFile);
        ConfigHandler.createFileIfNotExists(regionFile);

        // Files will never fail me
        return true;
    }

    private FileConfiguration getFileConfig(DataType dataType)
    {
        String path = dataType.getFilePath().replace("/", File.separator);
        File dataFile = new File(this.storageLocation + File.separator + path);
        FileConfiguration fileConfiguration = new YamlConfiguration();

        // Check if file exists before we load it
        if (!dataFile.exists())
        {
            return null;
        }

        try
        {
            // Try to load it
            fileConfiguration.load(dataFile);
        } catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }

        return fileConfiguration;
    }

    /**
     * Will return only the deepest keys that can be used in {@link nl.tim.questplugin.storage.Storage.DataPair}
     */
    private Set<String> getDeepKeys(FileConfiguration fileConfiguration, String section)
    {
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(section);
        Set<String> deepKeys = new HashSet<>();

        // Check if the section is empty (this key is the deepest possible key)
        if (configurationSection == null)
        {
            deepKeys.add(section);
            return deepKeys;
        }

        deepKeys.addAll(configurationSection.getKeys(false));

        Set<String> keys = new HashSet<>();

        // Go deeper!
        for (String key : deepKeys)
        {
            String newKey = section + "." + key;

            keys.addAll(this.getDeepKeys(fileConfiguration, newKey));
        }

        return keys;
    }

    @Override
    public void save(UUID uuid, DataType dataType, DataPair dataPair)
    {
        // Just create a array with the single value and pass on
        this.save(uuid, dataType, new DataPair[]{dataPair});
    }

    @Override
    public void save(UUID uuid, DataType dataType, DataPair[] dataPairs)
    {
        /*
        All data will be saved in the appropriate file (indicated by DataType.getFilePath()) in the following format:
        uuid:
            dataPair.getKey():  dataPair.getData()
         */

        String uid = uuid.toString();
        String path = dataType.getFilePath().replace("/", File.separator);
        File dataFile = new File(this.storageLocation + File.separator + path);
        FileConfiguration fileConfiguration = new YamlConfiguration();

        // Check if file exists, if not make one
        ConfigHandler.createFileIfNotExists(dataFile);

        try
        {
            // Loading file
            // Yes I know I have a function that does exactly this, but I need the dataFile later on anyways, sooo...
            fileConfiguration.load(dataFile);

            // Loop through all data
            for (DataPair dataPair : dataPairs)
            {
                // Create key and data pair
                String key = dataPair.getKey();
                Object data = dataPair.getData();

                // Save in file
                fileConfiguration.set(uid + "." + key, data);
            }

            // Save file
            fileConfiguration.save(dataFile);
        } catch (InvalidConfigurationException | IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public DataPair load(UUID uuid, DataType dataType, String key)
    {
        FileConfiguration fileConfiguration = this.getFileConfig(dataType);

        // Check if file config could be loaded
        if (fileConfiguration == null)
        {
            return null;
        }

        String result = fileConfiguration.getString(uuid.toString() + "." + key);

        return new DataPair<>(key, result);
    }

    @Override
    public List<DataPair> load(UUID uuid, DataType dataType)
    {
        FileConfiguration fileConfiguration = this.getFileConfig(dataType);
        List<DataPair> dataPairs = new ArrayList<>();
        List<UUID> uuidList = this.getSavedObjectsUID(dataType);

        // Check if the file config could be loaded
        if (fileConfiguration == null)
        {
            return null;
        }

        // Loop through all UUIDs
        for (UUID uid : uuidList)
        {
            String section = uid.toString();
            Set<String> allKeys = this.getDeepKeys(fileConfiguration, section);

            // Loop through all keys
            for (String key : allKeys)
            {
                // Create data pairs and add to result
                String data = fileConfiguration.getString(section + "." + key);
                DataPair dataPair = new DataPair<>(key, data);

                dataPairs.add(dataPair);
            }
        }

        return dataPairs;
    }

    @Override
    public List<UUID> getSavedObjectsUID(DataType dataType)
    {
        // Load file
        FileConfiguration fileConfiguration = this.getFileConfig(dataType);
        List<UUID> uuidList = new ArrayList<>();

        // Check if the file configuration could be loaded
        if (fileConfiguration == null)
        {
            return null;
        }

        // Loop through all keys
        for (String key : fileConfiguration.getKeys(false))
        {
            // Check if the key is actually a UUID, if it is add it to the list
            if (!key.matches("/^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/"))
            {
                uuidList.add(UUID.fromString(key));
            }
        }

        // Return the list
        return uuidList;
    }
}
