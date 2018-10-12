package nl.tim.questplugin;

import com.google.inject.Injector;
import nl.tim.questplugin.utils.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class QuestPlugin extends JavaPlugin
{
    // Path constants
    private static final String AREA_DATA_PATH = "data" + File.separator + "area";

    private Logger logger = getLogger();

    @Override
    public void onEnable() {
        // Running DI
        logger.info("Running dependency injector");

        QuestBinder questBinder = new QuestBinder(this, getDataFolder());
        Injector injector = questBinder.createInjector();

        // Inject all classes
        injector.injectMembers(this);

        // Continue normally
        logger.info("Loading handlers");

        LocationSerializer.configFolder = getDataFolder();

        LocationSerializer.saveLocation("data" + File.separator + "locs.yml", "area.locs.1", new Location(Bukkit.getWorld("world"), 12, 23.2, 124));

        // Done with loading
        logger.info("Loaded successfully");
    }

    @Override
    public void onDisable() {
        getLogger().info("Test plugin is unloading");
    }
}
