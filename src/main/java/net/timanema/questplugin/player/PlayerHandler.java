/*
 * Copyright (C) 2019  Tim Anema
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.timanema.questplugin.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.timanema.questplugin.QuestPlugin;
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
