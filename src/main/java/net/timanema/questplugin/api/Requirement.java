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
import net.timanema.questplugin.quest.CustomExtension;
import net.timanema.questplugin.quest.ExtensionType;
import net.timanema.questplugin.quest.stage.Stage;
import org.bukkit.entity.Player;

import java.util.Set;

public abstract class Requirement extends CustomExtension
{
    public Requirement(String displayName, String description)
    {
        super(displayName, description);
    }

    protected Stage getStage()
    {
        return (Stage) this.getOwner();
    }

    protected abstract boolean checkRequirement(Player player);

    public boolean requirementMet(Player player)
    {
        boolean negate = Boolean.valueOf(this.getSetting("negate").toString());

        return negate != this.checkRequirement(player);
    }

    @Override
    public Set<Storage.DataPair> getData()
    {
        Set<Storage.DataPair> data = super.getData();

        // Add type
        data.add(new Storage.DataPair<>("type", ExtensionType.REQUIREMENT.name()));

        return data;
    }
}
