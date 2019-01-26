/*
 * Copyright (C) 2019  Tim Anema
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.timanema.questplugin.storage.workers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.timanema.questplugin.QuestPlugin;
import net.timanema.questplugin.storage.Storage;
import net.timanema.questplugin.utils.StringUtils;
import net.timanema.questplugin.storage.ConfigHandler;
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
        QuestPlugin.getLog().info("Checking files");

        // Create all files
        for (DataType dataType : DataType.values())
        {
            File dataFile = new File(storageLocation + File.separator + dataType.getFilePath().replace("/", File.separator));

            ConfigHandler.createFileIfNotExists(dataFile);
        }

        // Files will never fail me
        return true;
    }

    @Override
    public void close()
    {
        // Files don't need closing
    }

    private FileConfiguration getFileConfig(DataType dataType)
    {
        if (dataType == null)
        {
            return null;
        }

        File dataFile = new File(this.storageLocation + File.separator + dataType.getFilePath().replace("/", File.separator));
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
     * Will return only the deepest keys that can be used in {@link DataPair}
     */
    private Set<String> getDeepKeys(FileConfiguration fileConfiguration, String section, boolean first)
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

            keys.addAll(this.getDeepKeys(fileConfiguration, newKey, false));
        }

        return keys;
    }

    @Override
    public void save(UUID uuid, DataType dataType, DataPair dataPair)
    {
        ArrayList<DataPair> dataPairs = new ArrayList<>();

        dataPairs.add(dataPair);
        this.save(uuid, dataType, dataPairs);
    }

    @Override
    public void save(UUID uuid, DataType dataType, Collection<DataPair> dataPairs)
    {
        /*
        All data will be saved in the appropriate file (indicated by DataType.getFilePath()) in the following format:

        uuid:
            dataPair.getKey():  dataPair.getData()
         */

        if (uuid == null || dataType == null || dataPairs == null)
        {
            return;
        }

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
                // Check if it's null
                if (dataPair == null)
                {
                    continue;
                }

                // Save in file
                fileConfiguration.set(uid + "." + dataPair.getKey(), dataPair.getData());
            }

            // Save file
            fileConfiguration.save(dataFile);
        } catch (InvalidConfigurationException | IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void saveAsList(UUID uuid, DataType dataType, DataPair<Collection<String>> dataPairs)
    {
        // Not needed in file based storage, will be done in save()
    }

    @Override
    public void saveAsInteger(UUID uuid, DataType dataType, Collection<DataPair<Integer>> dataPairs)
    {
        //TODO: Implement
    }

    @Override
    public void remove(UUID uuid, DataType dataType, String key)
    {
        // We can delete k/v pairs by just updating the value to null
        this.save(uuid, dataType, new DataPair<>(key, null));
    }

    @Override
    public DataPair load(UUID uuid, DataType dataType, String key)
    {
        FileConfiguration fileConfiguration = this.getFileConfig(dataType);

        // Check if file config could be loaded
        if (fileConfiguration == null || uuid == null)
        {
            return null;
        }

        String path = uuid.toString() + "." + key;

        // Check for collection
        if (fileConfiguration.isList(path))
        {
            return new DataPair<>(key, fileConfiguration.getList(path));
        } else
        {
            return new DataPair<>(key, fileConfiguration.getString(path));
        }
    }

    @Override
    public List<DataPair> load(UUID uuid, DataType dataType)
    {
        FileConfiguration fileConfiguration = this.getFileConfig(dataType);
        List<DataPair> dataPairs = new ArrayList<>();

        // Check if the file config could be loaded
        if (fileConfiguration == null)
        {
            return null;
        }

        if (uuid == null)
        {
            return null;
        }

        String section = uuid.toString();
        Set<String> allKeys = this.getDeepKeys(fileConfiguration, section, true);

        // Loop through all keys
        for (String key : allKeys)
        {
            // Create data pairs and add to result
            Object data;

            if (fileConfiguration.isList(key))
            {
                data = fileConfiguration.getList(key);
            } else
            {
                data = fileConfiguration.getString(key);
            }

            // Remove uuid if found
            if (!key.equals(uuid.toString()))
            {
                key = StringUtils.stripIncluding(key, uuid.toString(), true);
            }

            DataPair dataPair = new DataPair<>(key, data);

            dataPairs.add(dataPair);
        }

        return dataPairs;
    }

    @Override
    public Set<UUID> getSavedObjectsUID(DataType dataType)
    {
        // Load file
        FileConfiguration fileConfiguration = this.getFileConfig(dataType);
        Set<UUID> uuidList = new HashSet<>();

        // Check if the file configuration could be loaded
        if (fileConfiguration == null)
        {
            return null;
        }

        // Loop through all keys
        for (String key : fileConfiguration.getKeys(false))
        {
            // Check if the key is actually a UUID, if it is add it to the list
            if (StringUtils.isUUID(key))
            {
                uuidList.add(UUID.fromString(key));
            }
        }

        // Return the list
        return uuidList;
    }

    @Override
    public Set<UUID> searchForObject(DataType dataType, Set<UUID> uuids, DataPair<String> searchParam)
    {
        Set<UUID> matches = new HashSet<>();

        // Start matching
        for (UUID uuid : uuids)
        {
            if (this.load(uuid, dataType, searchParam.getKey()).getData().equals(searchParam.getData()))
            {
                matches.add(uuid);
            }
        }

        return matches;
    }
}
