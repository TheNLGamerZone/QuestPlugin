package nl.tim.questplugin;

import com.google.inject.Inject;
import com.google.inject.Injector;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.StorageProvider;
import nl.tim.questplugin.storage.image.builders.AreaImageBuilder;
import nl.tim.questplugin.storage.image.builders.PlayerImageBuilder;
import nl.tim.questplugin.storage.image.builders.QuestImageBuilder;
import nl.tim.questplugin.storage.image.builders.RegionImageBuilder;
import nl.tim.questplugin.utils.LocationSerializer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class QuestPlugin extends JavaPlugin
{
    public static StorageProvider.StorageType storageType;

    public static Logger logger;
    private Injector injector;

    @Inject private StorageProvider storageProvider;
    private Storage storage;

    @Inject private AreaImageBuilder areaImageBuilder;
    @Inject private QuestImageBuilder questImageBuilder;
    @Inject private PlayerImageBuilder playerImageBuilder;
    @Inject private RegionImageBuilder regionImageBuilder;

    @Override
    public void onEnable() {
        logger = getLogger();

        // Enable storage
        storageType = StorageProvider.StorageType.FILE_BASED;

        QuestPlugin.logger.info("Will use storage type: " + storageType.name());

        //TODO: Read config file to determine which storage type to use

        // Running DI
        logger.info("Running dependency injector");

        QuestBinder questBinder = new QuestBinder(this, getDataFolder());
        this.injector = questBinder.createInjector();

        // Inject all classes
        this.injector.injectMembers(this);

        // Continue normally
        logger.info("Loading handlers");

        this.storage = this.storageProvider.getStorage(storageType);
        LocationSerializer.configFolder = getDataFolder();

        this.storage.init();

        this.areaImageBuilder.save(null);

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
