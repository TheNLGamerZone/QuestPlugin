package nl.tim.questplugin.utils;

import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.storage.ConfigHandler;
import nl.tim.questplugin.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.inject.Singleton;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.UUID;

@Singleton
public final class LocationSerializer
{
    /**
     * Creates a {@link HashSet} containing {@link nl.tim.questplugin.storage.Storage.DataPair}s which can be saved
     * from a given {@link Location}.
     * @param location Location to serialize
     * @return Returns {@link HashSet} containing {@link nl.tim.questplugin.storage.Storage.DataPair}s representing
     * the given {@link Location}.
     */
    public static LinkedHashSet<Storage.DataPair<String>> serializeLocation(Location location)
    {
        LinkedHashSet<Storage.DataPair<String>> result = new LinkedHashSet<>();

        // Adding data
        result.add(new Storage.DataPair<>("world-uuid", location.getWorld().getUID().toString()));
        result.add(new Storage.DataPair<>("x", "" + location.getX()));
        result.add(new Storage.DataPair<>("y", "" + location.getY()));
        result.add(new Storage.DataPair<>("z", "" + location.getZ()));

        return result;
    }

    /**
     * Creates a {@link Location} based on given {@link nl.tim.questplugin.storage.Storage.DataPair}s.
     * @param data {@link nl.tim.questplugin.storage.Storage.DataPair}s used to build location
     * @return {@link Location} based on given {@link nl.tim.questplugin.storage.Storage.DataPair}s.
     */
    public static Location deserializeLocation(HashSet<Storage.DataPair<String>> data)
    {
        String[] location = new String[4];

        if (data.size() != 4)
        {
            QuestPlugin.getLog().warning("Invalid amount of location data supplied: '" + data.size() + "' (4 expected).");
            return null;
        }

        for (Storage.DataPair<String> dataPair : data)
        {
            String key = dataPair.getKey();
            int index = key.contains("world-uuid")  ? 0 :
                        key.contains("x")           ? 1 :
                        key.contains("y")           ? 2 :
                        key.contains("z")           ? 3 :
                                -1;

            if (index != -1)
            {
                location[index] = dataPair.getData();
            } else
            {
                QuestPlugin.getLog().warning("Invalid location key: '" + key + "' supplied.");
                return null;
            }
        }

        return new Location(
                Bukkit.getWorld(UUID.fromString(location[0])),
                Double.valueOf(location[1]),
                Double.valueOf(location[2]),
                Double.valueOf(location[3])
        );
    }
}
