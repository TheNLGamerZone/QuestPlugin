package nl.tim.questplugin.api;

import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.quest.CustomExtension;
import nl.tim.questplugin.quest.Quest;
import nl.tim.questplugin.storage.Storage;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public abstract class Trigger extends CustomExtension implements Listener
{
    public Trigger(String displayName, String description)
    {
        super(displayName, description);
    }

    protected Quest getQuest()
    {
        return (Quest) this.getOwner();
    }

    protected void triggerQuest(QPlayer player)
    {
        this.getQuestHandler().acceptQuest(player, this.getQuest());
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add reward
        data.add(new Storage.DataPair<>(this.getUUID() + ".trigger", this.getIdentifier()));

        // Add configuration
        Set<Storage.DataPair<String>> configuration = super.getData();

        configuration.forEach(dp -> dp.prependKey(this.getUUID() + "."));
        data.addAll(configuration);

        return data;
    }
}
