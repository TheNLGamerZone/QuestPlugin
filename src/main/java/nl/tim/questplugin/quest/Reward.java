package nl.tim.questplugin.quest;

import nl.tim.questplugin.player.PlayerHandler;
import nl.tim.questplugin.storage.Storage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class Reward extends Configurable
{
    private UUID uuid;
    private String identifier;

    private String displayName;
    private String description;

    private UUID parentUUID; //TODO: Implement methods to determine parent type

    private TaskHandler taskHandler;
    private QuestHandler questHandler;
    private PlayerHandler playerHandler;

    public Reward(String displayName, String description)
    {
        super();

        this.displayName = displayName;
        this.description = description;
    }

    public abstract void giveReward(Player player);

    protected void register(UUID uuid,
                            UUID parentUUID,
                            String identifier,
                            TaskHandler taskHandler,
                            QuestHandler questHandler,
                            PlayerHandler playerHandler,
                            Map<String, Object> settings)
    {
        this.uuid = uuid;
        this.parentUUID = parentUUID;
        this.identifier = identifier;
        this.taskHandler = taskHandler;
        this.questHandler = questHandler;
        this.playerHandler = playerHandler;
        this.copySettings(settings);
    }

    public String getIdentifier()
    {
        return this.identifier;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getDescription()
    {
        return this.description;
    }

    public UUID getRewardUUID()
    {
        return uuid;
    }

    protected UUID getParentUUID()
    {
        return this.parentUUID;
    }

    protected TaskHandler getTaskHandler()
    {
        return taskHandler;
    }

    protected QuestHandler getQuestHandler()
    {
        return questHandler;
    }

    protected PlayerHandler getPlayerHandler()
    {
        return playerHandler;
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add reward
        data.add(new Storage.DataPair<>(this.uuid + ".reward", this.identifier));

        // Add configuration
        Set<Storage.DataPair<String>> configuration = super.getData();

        configuration.forEach(dp -> dp.prependKey(this.uuid + "."));
        data.addAll(configuration);

        return data;
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

        Reward reward = (Reward) object;

        return new EqualsBuilder()
                .append(uuid, reward.uuid)
                .append(identifier, reward.identifier)
                .append(displayName, reward.displayName)
                .append(description, reward.description)
                .append(parentUUID, reward.parentUUID)
                .append(taskHandler, reward.taskHandler)
                .append(questHandler, reward.questHandler)
                .append(playerHandler, reward.playerHandler)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(uuid)
                .append(identifier)
                .append(displayName)
                .append(description)
                .append(parentUUID)
                .append(taskHandler)
                .append(questHandler)
                .append(playerHandler)
                .toHashCode();
    }
}
