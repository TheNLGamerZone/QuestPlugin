package nl.tim.questplugin.api;

import nl.tim.questplugin.player.PlayerHandler;
import nl.tim.questplugin.quest.Configurable;
import nl.tim.questplugin.quest.QuestHandler;
import nl.tim.questplugin.quest.TaskHandler;
import nl.tim.questplugin.storage.Saveable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;
import java.util.UUID;

public abstract class CustomExtension extends Configurable implements Saveable
{
    private UUID uuid;
    private String identifier;

    private TaskHandler taskHandler;
    private QuestHandler questHandler;
    private PlayerHandler playerHandler;

    public void register(UUID uuid,
                            String identifier,
                            TaskHandler taskHandler,
                            QuestHandler questHandler,
                            PlayerHandler playerHandler,
                            Map<String, Object> settings)
    {
        this.uuid = uuid;
        this.identifier = identifier;
        this.taskHandler = taskHandler;
        this.questHandler = questHandler;
        this.playerHandler = playerHandler;
        this.copySettings(settings);
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public String getIdentifier()
    {
        return this.identifier;
    }


    protected TaskHandler getTaskHandler()
    {
        return this.taskHandler;
    }

    protected QuestHandler getQuestHandler()
    {
        return this.questHandler;
    }

    protected PlayerHandler getPlayerHandler()
    {
        return this.playerHandler;
    }

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

        CustomExtension that = (CustomExtension) object;

        return new EqualsBuilder()
                .append(uuid, that.uuid)
                .append(identifier, that.identifier)
                .append(taskHandler, that.taskHandler)
                .append(questHandler, that.questHandler)
                .append(playerHandler, that.playerHandler)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(uuid)
                .append(identifier)
                .append(taskHandler)
                .append(questHandler)
                .append(playerHandler)
                .toHashCode();
    }
}
