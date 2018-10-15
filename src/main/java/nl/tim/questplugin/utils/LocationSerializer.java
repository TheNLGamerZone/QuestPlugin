package nl.tim.questplugin.utils;

import nl.tim.questplugin.storage.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Singleton
@Deprecated
public final class LocationSerializer
{
    /*
    This class is currently obsolete because of the new file storage
    Will rewrite this to generate DataPairs instead to make saving locations easier
     */
    public static File configFolder;

    public static void saveLocation(String path, String internalPath, Location location)
    {
        saveLocation(path, internalPath, location, true);
    }

    public static void saveLocation(String path, String internalPath, Location location, boolean save)
    {
        if (location == null)
        {
            return;
        }

        File dataFile = new File(configFolder + File.separator + path);
        FileConfiguration fileConfiguration = new YamlConfiguration();

        ConfigHandler.createFileIfNotExists(dataFile);

        try
        {
            // Loading file
            fileConfiguration.load(dataFile);

            // Seperating data
            UUID uuid = location.getWorld().getUID();
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();

            fileConfiguration.set(internalPath + ".world-uuid", uuid.toString());
            fileConfiguration.set(internalPath + ".x", x);
            fileConfiguration.set(internalPath + ".y", y);
            fileConfiguration.set(internalPath + ".z", z);

            if (save)
            {
                fileConfiguration.save(dataFile);
            }
        } catch (InvalidConfigurationException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public static Location loadLocation(String path, String internalPath)
    {
        File dataFile = new File(configFolder + File.separator + path);
        FileConfiguration fileConfiguration = new YamlConfiguration();

        if (!dataFile.exists())
        {
            return null;
        }

        try {
            fileConfiguration.load(dataFile);
        } catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }

        // Collect data
        UUID uuid = UUID.fromString(fileConfiguration.getString(internalPath + ".world-uuid"));
        double x = fileConfiguration.getDouble(internalPath + ".x");
        double y = fileConfiguration.getDouble(internalPath + ".y");
        double z = fileConfiguration.getDouble(internalPath + ".z");

        return new Location(Bukkit.getWorld(uuid), x, y , z);
    }
}
