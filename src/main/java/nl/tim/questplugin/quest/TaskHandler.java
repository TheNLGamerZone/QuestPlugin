package nl.tim.questplugin.quest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.quest.stage.Requirement;
import nl.tim.questplugin.quest.stage.Reward;
import nl.tim.questplugin.quest.wrappers.TaskWrapper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class TaskHandler
{
    private Set<Task> tasks;
    private Set<Requirement> requirements;
    private Set<Reward> rewards;

    private Set<TaskWrapper> taskWrappers;

    private QuestPlugin questPlugin;

    @Inject
    public TaskHandler(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
        this.tasks = new HashSet<>();
        this.requirements = new HashSet<>();
        this.rewards = new HashSet<>();
        this.taskWrappers = new HashSet<>();
    }

    /**
     * Registers given {@link Task}. This will also register the listener with Bukkit's {@link org.bukkit.plugin.PluginManager}.
     * @param task {@link Task} to register
     */
    public void registerTask(Task task)
    {
        // Check to prevent issues with registering listeners
        if (!this.tasks.contains(task))
        {
            this.questPlugin.getServer().getPluginManager().registerEvents(task, this.questPlugin);
            this.tasks.add(task);
            task.register(this, this.questPlugin.getQuestHandler(), this.questPlugin.getPlayerHandler());
        }
    }

    /**
     * Registers given {@link Requirement}.
     * @param requirement {@link Requirement} to register
     */
    public void registerRequirement(Requirement requirement)
    {
        this.requirements.add(requirement);
    }

    /**
     * Registers given {@link Reward}.
     * @param reward {@link Reward} to register
     */
    public void registerReward(Reward reward)
    {
        this.rewards.add(reward);
    }

    public void registerTaskWrapper(TaskWrapper taskWrapper)
    {
        this.taskWrappers.add(taskWrapper);
    }

    public Set<Task> getTasks()
    {
        return this.tasks;
    }

    public Set<Requirement> getRequirements()
    {
        return this.requirements;
    }

    public Set<Reward> getRewards()
    {
        return this.rewards;
    }

    public Set<TaskWrapper> getTaskWrappers()
    {
        return this.taskWrappers;
    }

    public TaskWrapper getTaskWrapper(UUID uuid)
    {
        for (TaskWrapper wrapper : this.taskWrappers)
        {
            if (wrapper.getUUID().equals(uuid))
            {
                return wrapper;
            }
        }

        return null;
    }
}
