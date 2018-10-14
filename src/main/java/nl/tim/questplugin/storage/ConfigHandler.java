package nl.tim.questplugin.storage;

import nl.tim.questplugin.QuestPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigHandler
{
    private QuestPlugin questPlugin;

    public ConfigHandler(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
    }

    public void init()
    {
        this.questPlugin.saveResource("config.yml", false);
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

    public static void checkFileExists(File file)
    {
        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            try {
                QuestPlugin.logger.info("Creating file " + file.getName());
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
