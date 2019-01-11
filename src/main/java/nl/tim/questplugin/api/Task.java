package nl.tim.questplugin.api;

import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.quest.CustomExtension;
import nl.tim.questplugin.quest.Owner;
import nl.tim.questplugin.quest.stage.Stage;
import nl.tim.questplugin.storage.Storage;
import org.bukkit.event.Listener;

import java.util.*;

public abstract class Task extends CustomExtension implements Listener, Owner
{
    public Task(String displayName, String description)
    {
        super(displayName, description);
    }

    public Stage getStage()
    {
        return (Stage) this.getOwner();
    }

    protected Integer getProgress(QPlayer player)
    {
        return player.getProgress(this.getUUID()).getProgress();
    }

    protected void updateProgess(QPlayer player, Integer newProgress)
    {
        player.getProgress(this.getUUID()).updateProgress(newProgress);
    }

    public abstract Integer getRequiredProgressToFinish();

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add task identifier
        data.add(new Storage.DataPair<>(this.getUUID() + ".task", this.getIdentifier()));
        data.add(new Storage.DataPair<>(this.getUUID() + ".stage", this.getStage().getUUID().toString()));

        // Add configuration
        Set<Storage.DataPair<String>> configuration = super.getData();

        configuration.forEach(dp -> dp.prependKey(this.getUUID() + "."));
        data.addAll(configuration);

        return data;
    }
}
