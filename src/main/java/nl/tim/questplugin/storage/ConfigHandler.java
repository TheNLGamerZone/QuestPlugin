package nl.tim.questplugin.storage;

import nl.tim.questplugin.QuestPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigHandler
{
    private QuestPlugin questPlugin;

    public ConfigHandler(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
    }

    public static void checkFileExists(File file)
    {
        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
