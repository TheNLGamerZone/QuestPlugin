package nl.tim.questplugin.storage;

import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConfigHandler
{
    private QuestPlugin questPlugin;
    private File dataFolder;

    public ConfigHandler(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
    }

    public void init()
    {
        this.questPlugin.saveResource("config.yml", false);

        this.dataFolder = this.questPlugin.getDataFolder();

        // Check for update
        if (!compareVersions(Constants.NEWEST_CONFIG_VERSION))
        {
            QuestPlugin.getLog().warning("Your config.yml file is outdated, this might cause the plugin to malfunction!");
            QuestPlugin.getLog().warning("Consider updating with /quests config update");
        }
    }

    public <T> T getOption(Class<T> type, String option)
    {
        String result = this.questPlugin.getConfig().getString(option);

        // Check if it was a valid option
        if (result == null || !(this.questPlugin.getConfig().isSet(option)))
        {
            return null;
        }

        // If so return correct type
        if (type == Boolean.class)
        {
            return type.cast(Boolean.valueOf(result));
        } else if (type == Integer.class)
        {
            return type.cast(Integer.valueOf(result));
        } else if (type == Long.class)
        {
            return type.cast(Long.valueOf(result));
        } else if (type == Double.class)
        {
            return type.cast(Double.valueOf(result));
        } else
        {
            // Just return a string
            return type.cast(result);
        }
    }

    public List getOptionList(String option)
    {
        return this.questPlugin.getConfig().getList(option);
    }

    public List<String> getOptionStringList(String option)
    {
        return this.questPlugin.getConfig().getStringList(option);
    }

    /**
     * Checks if the config.yml has an older version
     * @return true if the config.yml file is up-to-date, false otherwise
     */
    public boolean compareVersions(String compareTo)
    {
        String oldVersion = this.getOption(String.class, "config_version");

        return oldVersion.equals(compareTo);
    }

    //TODO: 'connect this with command /quests config update'
    /**
     * Method to update config, should it change, while maintaining old settings
     */
    public boolean updateConfig(String compareTo)
    {
        String configPath = new File("").getAbsolutePath() + File.separator + this.dataFolder + File.separator + "config.yml";

        // Check if config.yml still exists and create if not
        this.questPlugin.saveResource("config.yml", false);

        // Starting overwriting
        QuestPlugin.getLog().info("Location of config file: " + configPath);
        QuestPlugin.getLog().info("Updating to version:" + compareTo);

        // Check if we have to update
        if (compareVersions(compareTo))
        {
            QuestPlugin.getLog().warning("Config is already up-to-date!");
            return false;
        }

        // Getting old settings
        List<Storage.DataPair<String>> oldSettings = this.getDataFromOldConfig(this.questPlugin.getConfig().getKeys(false));


        // Create new config file
        this.questPlugin.saveResource("config.yml", true);

        try
        {
            Path path = Paths.get(configPath);
            List<String> lines = Files.readAllLines(path);
            List<String> editedLines = new ArrayList<>();

            // Looping through all lines from file and old settings
            for (String line : lines)
            {
                boolean replaced = false;

                // Only start comparing keys if this line is not a comment
                if (!(line.startsWith("#")))
                {
                    for (Storage.DataPair<String> dataPair : oldSettings)
                    {
                        String key = dataPair.getKey();
                        String data = dataPair.getData();
                        String oldData = line.split(":\\s").length > 1 ?
                                line.split(":\\s")[1] :
                                "null";

                        // If the current line has a saved value, replace the default value
                        if (line.startsWith(key))
                        {
                            if (data.equals(oldData))
                            {
                                // The saved value was never changed, so we're just skipping this line
                                break;
                            }

                            QuestPlugin.getLog().info("Replacing default value for '" + key + "' with saved value '" + data + "'");
                            editedLines.add(key + ": " + data);

                            // Set replaced flag
                            replaced = true;
                            break;
                        }
                    }
                }

                // If the value has not been replaced by any other old value, just keep the default value
                if (!replaced)
                {
                    editedLines.add(line);
                }
            }

            Files.write(path, editedLines);
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    /**
     * Returns all old settings from the given keys
     * @param keys
     * @return
     */
    private List<Storage.DataPair<String>> getDataFromOldConfig(Set<String> keys)
    {
        List<Storage.DataPair<String>> oldData = new ArrayList<>();

        // Looping through all keys and saving old data (settings)
        for (String key : keys)
        {
            // Check if current key is the config version one, we obviously do not want to save that
            if (key.equals(Constants.CONFIG_VERSION_OPTION))
            {
                continue;
            }

            String data = this.getOption(String.class, key);
            Storage.DataPair<String> dataPair = new Storage.DataPair<>(key, data);

            QuestPlugin.getLog().info("Saving setting '" + key + "', with value '" + data + "'");

            // Add to array
            oldData.add(dataPair);
        }

        return oldData;
    }

    /**
     * Creates a file if it does not yet exist.
     * @param file file to create
     */
    public static void createFileIfNotExists(File file)
    {
        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            try
            {
                QuestPlugin.getLog().info("Creating file " + file.getName());
                file.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
