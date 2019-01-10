package nl.tim.questplugin.quest;

import nl.tim.questplugin.api.CustomExtension;
import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.storage.Storage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public abstract class Trigger extends CustomExtension implements Listener
{
    private String displayName;
    private String description;

    private Quest quest;

    public Trigger(String displayName, String description)
    {
        this.displayName = displayName;
        this.description = description;
    }

    protected void register(Quest quest)
    {
        this.quest = quest;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getDescription()
    {
        return this.description;
    }

    protected QPlayer getPlayer(Player player)
    {
        return this.getPlayerHandler().getPlayer(player);
    }

    protected void trigger(QPlayer player)
    {
        this.getQuestHandler().acceptQuest(player, this.quest);
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

        Trigger trigger = (Trigger) object;

        return new EqualsBuilder()
                .appendSuper(super.equals(object))
                .append(displayName, trigger.displayName)
                .append(description, trigger.description)
                .append(quest, trigger.quest)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(displayName)
                .append(description)
                .append(quest)
                .toHashCode();
    }
}
