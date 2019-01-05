package nl.tim.questplugin.quest;

import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.player.QPlayer;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.UUID;

public abstract class Task implements Listener
{
    private UUID uuid;
    private String name;
    private String displayName;
    private int requiredPoints;
    private TaskHandler taskHandler;

    public Task(UUID uuid, String name, String displayName, int requiredPoints)
    {
        this.uuid = uuid;
        this.name = name;
        this.displayName = displayName;
        this.requiredPoints = requiredPoints;
    }

    public void incrementScore(QPlayer player)
    {
        this.addPoints(player, 1);
    }

    public void addPoints(QPlayer player, int amount)
    {
        //TODO: Implement + remove debug
        QuestPlugin.getLog().severe("Incremented score by " + amount);
    }

    protected void register(TaskHandler taskHandler)
    {
        this.taskHandler = taskHandler;
    }

    public int getRequiredPoints()
    {
        return this.requiredPoints;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getName()
    {
        return this.name;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.uuid, this.name, this.displayName, this.requiredPoints);
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (!(object instanceof Task))
        {
            return false;
        }

        Task task = (Task) object;

        return new EqualsBuilder()
                .append(this.uuid, task.getUUID())
                .append(this.name, task.getName())
                .append(this.displayName, task.getDisplayName())
                .append(this.requiredPoints, task.getRequiredPoints())
                .isEquals();
    }
}
