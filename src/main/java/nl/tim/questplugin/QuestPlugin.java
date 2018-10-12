package nl.tim.questplugin;

import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class QuestPlugin extends JavaPlugin
{
    private Logger logger = getLogger();

    @Override
    public void onEnable() {
        // Running DI
        logger.info("Running dependency injector");

        QuestBinder questBinder = new QuestBinder(this);
        Injector injector = questBinder.createInjector();

        // Inject all classes
        injector.injectMembers(this);

        // Continue normally
        logger.info("Loading handlers");



        // Done with loading
        logger.info("Loaded successfully");
    }

    @Override
    public void onDisable() {
        getLogger().info("Test plugin is unloading");
    }
}
