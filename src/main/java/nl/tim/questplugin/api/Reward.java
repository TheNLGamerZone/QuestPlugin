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

package nl.tim.questplugin.api;

import nl.tim.questplugin.quest.CustomExtension;
import nl.tim.questplugin.quest.ExtensionType;
import nl.tim.questplugin.quest.Owner;
import nl.tim.questplugin.storage.Storage;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public abstract class Reward extends CustomExtension
{
    public Reward(String displayName, String description)
    {
        super(displayName, description);
    }

    public abstract void giveReward(Player player);

    public UUID getParentUUID()
    {
        return this.getOwner().getUUID();
    }

    protected Owner getParent()
    {
        return this.getOwner();
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = super.getData();

        // Add type
        data.add(new Storage.DataPair<>("type", ExtensionType.REWARD.name()));

        return data;
    }
}
