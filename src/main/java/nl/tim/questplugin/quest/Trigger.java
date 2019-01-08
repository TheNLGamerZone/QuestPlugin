package nl.tim.questplugin.quest;

import nl.tim.questplugin.player.PlayerHandler;
import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.quest.wrappers.TriggerWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public abstract class Trigger implements Listener
{
    private String identifier;
    private String displayName;
    private String description;

    private QuestHandler questHandler;
    private PlayerHandler playerHandler;

    public Trigger(String identifier, String displayName, String description)
    {
        this.identifier = identifier;
        this.displayName = displayName;
        this.description = description;
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

    protected QPlayer getPlayer(Player player)
    {
        return this.playerHandler.getPlayer(player);
    }

    protected Set<Quest> getQuests()
    {
        Set<Quest> listeningQuests = new HashSet<>();

        for (Quest quest : this.questHandler.getQuests())
        {
            for (TriggerWrapper trigger : quest.getTriggers())
            {
                if (trigger.getTrigger().getIdentifier().equals(this.identifier))
                {
                    listeningQuests.add(quest);
                }
            }
        }

        return listeningQuests;
    }

    protected Set<TriggerWrapper> getTriggersForQuest(Quest quest)
    {
        return quest.getTriggers();
    }

    protected void trigger(Quest quest, QPlayer player)
    {
        this.questHandler.acceptQuest(player, quest);
    }

    protected Object getSetting(TriggerWrapper wrapper)
    {
        return wrapper.getSetting();
    }

    protected void register(QuestHandler questHandler, PlayerHandler playerHandler)
    {
        this.questHandler = questHandler;
        this.playerHandler = playerHandler;
    }
}
