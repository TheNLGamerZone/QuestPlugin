package nl.tim.questplugin.quest;

import nl.tim.questplugin.player.PlayerHandler;
import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.quest.stage.Stage;
import nl.tim.questplugin.storage.Saveable;
import nl.tim.questplugin.storage.Storage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public abstract class Task extends Configurable implements Listener, Saveable
{
    private UUID uuid;
    private String identifier;
    private String displayName;

    private Stage stage;

    private TaskHandler taskHandler;
    private QuestHandler questHandler;
    private PlayerHandler playerHandler;

    public Task(String displayName)
    {
        super();

        this.displayName = displayName;
    }

    protected void register(Stage stage,
                            UUID uuid,
                            String identifier,
                            TaskHandler taskHandler,
                            QuestHandler questHandler,
                            PlayerHandler playerHandler,
                            Map<String, Object> settings)
    {
        this.stage = stage;
        this.uuid = uuid;
        this.identifier = identifier;
        this.taskHandler = taskHandler;
        this.questHandler = questHandler;
        this.playerHandler = playerHandler;
        this.copySettings(settings);
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

    protected Stage getStage()
    {
        return this.stage;
    }

    public UUID getTaskUUID()
    {
        return this.uuid;
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

    public abstract Integer getRequiredProgressToFinish();

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add task identifier
        data.add(new Storage.DataPair<>(uuid + ".task", this.identifier));
        data.add(new Storage.DataPair<>(uuid + ".stage", this.stage.getUUID().toString()));

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

        Task task = (Task) object;

        return new EqualsBuilder()
                .append(uuid, task.uuid)
                .append(identifier, task.identifier)
                .append(displayName, task.displayName)
                .append(stage, task.stage)
                .append(this.getSettingDescriptions(), task.getSettingDescriptions())
                .append(this.getConfigurationValues(), task.getConfigurationValues())
                .append(taskHandler, task.taskHandler)
                .append(questHandler, task.questHandler)
                .append(playerHandler, task.playerHandler)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(uuid)
                .append(identifier)
                .append(displayName)
                .append(stage)
                .append(this.getSettingDescriptions())
                .append(this.getConfigurationValues())
                .append(taskHandler)
                .append(questHandler)
                .append(playerHandler)
                .toHashCode();
    }
}
