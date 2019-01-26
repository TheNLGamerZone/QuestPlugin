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

package net.timanema.questplugin.api;

import net.timanema.questplugin.storage.Storage;
import net.timanema.questplugin.player.QPlayer;
import net.timanema.questplugin.quest.CustomExtension;
import net.timanema.questplugin.quest.ExtensionType;
import net.timanema.questplugin.quest.Quest;
import org.bukkit.event.Listener;

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
    public Set<Storage.DataPair> getData()
    {
        Set<Storage.DataPair> data = super.getData();

        // Add type
        data.add(new Storage.DataPair<>("type", ExtensionType.TRIGGER.name()));

        return data;
    }
}
