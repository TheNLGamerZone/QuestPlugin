package nl.tim.questplugin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import nl.tim.questplugin.storage.FileStorage;
import nl.tim.questplugin.storage.Storage;

import java.io.File;

public class QuestBinder extends AbstractModule
{
    private QuestPlugin questPlugin;
    private File configFolder;

    /**
     * Constructor for binder
     * @param questPlugin
     */
    public QuestBinder(QuestPlugin questPlugin, File configFolder)
    {
        this.questPlugin = questPlugin;
        this.configFolder = configFolder;
    }

    public Injector createInjector()
    {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(QuestPlugin.class).toInstance(this.questPlugin);

        // TODO: Make this change according to storage setting
        this.bind(Storage.class).to(FileStorage.class);

        this.bind(File.class).annotatedWith(Names.named("config")).toInstance(configFolder);
    }
}
