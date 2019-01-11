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

package nl.tim.questplugin.quest.stage.rewards;

import nl.tim.questplugin.api.ExtensionInformation;
import nl.tim.questplugin.api.InputType;
import nl.tim.questplugin.api.Reward;
import org.bukkit.entity.Player;

/**
 * Advanced reward used for linking floating stages to a quest, to implement branching quests.
 * Setting: {@link java.util.UUID}
 * Behaviour: Link the set {@link nl.tim.questplugin.quest.stage.Stage} to the quest
 */
@ExtensionInformation(identifier = "stage_link", author = "Tim")
public class StageLinkReward extends Reward
{
    public StageLinkReward()
    {
        super("Link another stage (branching)",
                "Upon receiving this reward the set stage will be linked to the quest. " +
                        "When a link is established between a quest and stage, the other branches of the original quest will be cancelled for the player." +
                        "The linked stages will be displayed as a regular stage in the player progress view. " +
                        "Only floating stages can be linked.");
    }

    @Override
    public void init()
    {
        this.addConfiguration("stage_to_link", "ID of the stage to link", InputType.STRING);
    }

    @Override
    public void giveReward(Player player)
    {
        //TODO: Implement
    }
}
