package nl.tim.questplugin.quest;

import nl.tim.questplugin.quest.tasks.TaskOption;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;

public abstract class Task implements Listener
{
    private String identifier;
    private String displayName;
    private TaskHandler taskHandler;

    public Task(String identifier, String displayName)
    {
        this.identifier = identifier;
        this.displayName = displayName;
    }

    protected void register(TaskHandler taskHandler)
    {
        this.taskHandler = taskHandler;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getIdentifier()
    {
        return this.identifier;
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
    public int hashCode()
    {
        return Objects.hash(this.identifier, this.displayName);
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (object.getClass() != this.getClass())
        {
            return false;
        }

        Task task = (Task) object;

        return new EqualsBuilder()
                .append(this.getRequiredConfiguration(), task.getRequiredConfiguration())
                .append(this.identifier, task.getIdentifier())
                .append(this.displayName, task.getDisplayName())
                .isEquals();
    }
}
