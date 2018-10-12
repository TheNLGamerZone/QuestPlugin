package nl.tim.questplugin;

import com.google.inject.Inject;
import com.google.inject.Injector;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.image.builders.AreaImageBuilder;
import nl.tim.questplugin.storage.image.builders.QuestImageBuilder;
import nl.tim.questplugin.utils.LocationSerializer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class QuestPlugin extends JavaPlugin
{
    // Path constants
    private static final String AREA_DATA_PATH = "data" + File.separator + "area";
    private static final String QUEST_DATA_PATH = "data" + File.separator + "quest";
    private static final String PLAYER_DATA_PATH = "data" + File.separator + "player";

    private Logger logger = getLogger();
    private Injector injector;

    @Inject
    private Storage storage;

    @Inject
    private AreaImageBuilder areaImageBuilder;

    @Inject
    private QuestImageBuilder questImageBuilder;

    @Override
    public void onEnable() {
        // Enable storage

        //TODO: Read config file to determine which storage type to use

        // Running DI
        logger.info("Running dependency injector");

        QuestBinder questBinder = new QuestBinder(this, getDataFolder());
        injector = questBinder.createInjector();

        // Inject all classes
        injector.injectMembers(this);

        // Continue normally
        logger.info("Loading handlers");

        LocationSerializer.configFolder = getDataFolder();

        storage.init();
        // Done with loading
    }

    @Override
    public void onDisable() {
        getLogger().info("Test plugin is unloading");
    }

    public Injector getInjector()
    {
        return this.injector;
    }

    public Storage getStorage()
    {
        return this.storage;
    }
}
