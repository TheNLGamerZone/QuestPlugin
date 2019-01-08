package nl.tim.questplugin.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.QuestPlugin;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class PlayerHandler
{
    private QuestPlugin questPlugin;
    private Set<QPlayer> players;

    @Inject
    public PlayerHandler(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
        this.players = new HashSet<>();
    }

    public Set<QPlayer> getPlayers()
    {
        return this.players;
    }

    public QPlayer getPlayer(Player player)
    {
        for (QPlayer currentPlayer : this.players)
        {
            if (currentPlayer.getUUID().equals(player.getUniqueId()))
            {
                return currentPlayer;
            }
        }

        return null;
    }

    public Player getPlayer(QPlayer player)
    {
        for (Player currentPlayer : questPlugin.getServer().getOnlinePlayers())
        {
            if (currentPlayer.getUniqueId().equals(player.getUUID()))
            {
                return currentPlayer;
            }
        }

        return null;
    }
}
