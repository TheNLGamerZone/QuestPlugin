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

package net.timanema.questplugin.quest.stage.requirements;

import net.timanema.questplugin.api.InputType;
import net.timanema.questplugin.api.Requirement;
import net.timanema.questplugin.api.ExtensionInformation;
import org.bukkit.entity.Player;

/**
 * Very simple requirement, mainly used for testing.
 * Setting: {@link String}
 * Behaviour: Returns true if the player display name is equal to the given setting
 */
@ExtensionInformation(identifier = "req_name", author = "Tim")
public class NameRequirement extends Requirement
{
    public NameRequirement()
    {
        super("Player name", "Player name should be equal to the setting");
    }

    @Override
    public void init()
    {
        this.addConfiguration("name", "Player name will be compared to this", InputType.STRING);
    }

    @Override
    public boolean checkRequirement(Player player)
    {
        String requiredName = (String) this.getSetting("name");

        return player.getDisplayName().equals(requiredName);
    }
}
