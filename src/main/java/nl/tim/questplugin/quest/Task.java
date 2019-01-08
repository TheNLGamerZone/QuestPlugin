package nl.tim.questplugin.quest;

import nl.tim.questplugin.player.PlayerHandler;
import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.quest.tasks.TaskOption;
import nl.tim.questplugin.quest.wrappers.TaskWrapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class Task implements Listener
{
    private String identifier;
    private String displayName;

    private TaskHandler taskHandler;
    private QuestHandler questHandler;
    private PlayerHandler playerHandler;

    public Task(String identifier, String displayName)
    {
        this.identifier = identifier;
        this.displayName = displayName;
    }

    protected void register(TaskHandler taskHandler, QuestHandler questHandler, PlayerHandler playerHandler)
    {
        this.taskHandler = taskHandler;
        this.questHandler = questHandler;
        this.playerHandler = playerHandler;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getIdentifier()
    {
        return this.identifier;
    }

    public QPlayer getPlayer(Player player)
    {
        return this.playerHandler.getPlayer(player);
    }

    protected Set<Quest> getQuestsForPlayer(Player player)
    {
        QPlayer qPlayer = this.getPlayer(player);

        return qPlayer.getProgressWrappers()
                .stream()
                .map(pw -> this.questHandler.getQuest(pw.getQuestUUID()))
                .collect(Collectors.toSet());
    }

    protected Set<TaskWrapper> getTasksForPlayer(Player player, Quest quest)
    {
        QPlayer qPlayer = this.getPlayer(player);

        return qPlayer.getProgressWrappers()
                .stream()
                .filter(pw -> pw.getQuestUUID().equals(quest.getUUID()))
                .map(pw -> this.taskHandler.getTaskWrapper(pw.getTaskUUID()))
                .filter(tw -> tw.getTask().getIdentifier().equals(this.identifier))
                .collect(Collectors.toSet());
    }

    protected Object getTaskOption(TaskWrapper taskWrapper, TaskOption taskOption)
    {
        return taskWrapper.getTaskConfiguration().get(taskOption);
    }

    /**
     * Returns a {@link Set<TaskOption>} containing all configuration options that should be set in order to
     * make the task work as intended. This way tasks can be reconfigured to behave as the plugin configurer intended.
     * @return A {@link Set<TaskOption>} containing all configuration options for this task.
     */
    public abstract Set<TaskOption> getRequiredConfiguration();

    /**
     * Returns a {@link TaskOption} which will be the one the decides when a task is completed. The {@link TaskHandler}
     * will compare the value configured by the given {@link TaskOption} to the amount of progress a player has made.
     * The option returned should be in the set returned by {@link #getRequiredConfiguration()}, otherwise the task will
     * be marked as broken. This can also only be a {@link TaskOption} that accepts integers, for obvious reasons.
     * Sidenote: In case none of the task options fulfill your needs, {@link TaskOption#WILDCARD} can be used as a temporary
     * placeholder. Create an issue or PR on github to request your desired task option.
     * @return A {@link TaskOption} that 'decides' when a task is finished.
     */
    public abstract TaskOption getFinishOption();

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (object == null || getClass() != object.getClass())
        {
            return false;
        }

        Task task = (Task) object;

        return new EqualsBuilder()
                .append(identifier, task.identifier)
                .append(displayName, task.displayName)
                .append(taskHandler, task.taskHandler)
                .append(questHandler, task.questHandler)
                .append(playerHandler, task.playerHandler)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(identifier)
                .append(displayName)
                .append(taskHandler)
                .append(questHandler)
                .append(playerHandler)
                .toHashCode();
    }
}
