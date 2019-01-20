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

package net.timanema.questplugin.quest.stage.rewards;

import net.timanema.questplugin.api.InputType;
import net.timanema.questplugin.api.Reward;
import net.timanema.questplugin.api.ExtensionInformation;
import org.bukkit.entity.Player;

/**
 * Very simple reward, mainly used for testing. This reward is just a placeholder for sending messages upon
 * completion of a task/stage/quest
 * Setting: {@link String}
 * Behaviour: Send the message set as the setting to the player
 */
@ExtensionInformation(identifier = "message_reward", author = "Tim")
public class MessageReward extends Reward
{
    public MessageReward()
    {
        super("msg",
                "User should not see this");
    }

    @Override
    public void init()
    {
        this.addConfiguration("msg", "Message to send", InputType.STRING);
    }

    @Override
    public void giveReward(Player player)
    {
        player.sendMessage(this.getSetting("msg").toString());
    }
}
