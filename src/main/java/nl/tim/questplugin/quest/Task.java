package nl.tim.questplugin.quest;

import nl.tim.questplugin.api.CustomExtension;
import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.quest.stage.Stage;
import nl.tim.questplugin.storage.Storage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public abstract class Task extends CustomExtension implements Listener
{
    private String displayName;
    private Stage stage;

    public Task(String displayName)
    {
        super();

        this.displayName = displayName;
    }

    protected void register(Stage stage)
    {
        this.stage = stage;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public QPlayer getPlayer(Player player)
    {
        return this.getPlayerHandler().getPlayer(player);
    }

    protected Stage getStage()
    {
        return this.stage;
    }

    public abstract Integer getRequiredProgressToFinish();

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add task identifier
        data.add(new Storage.DataPair<>(this.getUUID() + ".task", this.getIdentifier()));
        data.add(new Storage.DataPair<>(this.getUUID() + ".stage", this.stage.getUUID().toString()));

        // Add configuration
        Set<Storage.DataPair<String>> configuration = super.getData();

        configuration.forEach(dp -> dp.prependKey(this.getUUID() + "."));
        data.addAll(configuration);

        return data;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Task task = (Task) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(displayName, task.displayName)
                .append(stage, task.stage)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(displayName)
                .append(stage)
                .toHashCode();
    }
}
