package nl.tim.questplugin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class QuestBinder extends AbstractModule
{
    private final QuestPlugin questPlugin;

    /**
     * Constructor for binder
     * @param questPlugin
     */
    public QuestBinder(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
    }

    public Injector createInjector()
    {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(QuestPlugin.class).toInstance(this.questPlugin);
    }
}
