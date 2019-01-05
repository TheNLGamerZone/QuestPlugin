package nl.tim.questplugin.quest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.QuestPlugin;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class TaskHandler
{
    private Set<Task> tasks;
    private QuestPlugin questPlugin;

    @Inject
    public TaskHandler(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
        this.tasks = new HashSet<>();
    }

    /**
     * Registers given {@link Task}.
     * @param task
     */
    public void registerTask(Task task)
    {
        if (!this.tasks.contains(task))
        {
            this.questPlugin.getServer().getPluginManager().registerEvents(task, this.questPlugin);
            this.tasks.add(task);
            task.register(this);
        }
    }
}
