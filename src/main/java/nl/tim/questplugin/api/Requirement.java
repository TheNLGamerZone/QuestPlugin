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
import nl.tim.questplugin.quest.stage.Stage;
import nl.tim.questplugin.storage.Storage;
import org.bukkit.entity.Player;

import java.util.HashSet;
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
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add reward
        data.add(new Storage.DataPair<>(this.getUUID() + ".requirement", this.getIdentifier()));

        // Add configuration
        Set<Storage.DataPair<String>> configuration = super.getData();

        configuration.forEach(dp -> dp.prependKey(this.getUUID() + "."));
        data.addAll(configuration);

        return data;
    }
}
