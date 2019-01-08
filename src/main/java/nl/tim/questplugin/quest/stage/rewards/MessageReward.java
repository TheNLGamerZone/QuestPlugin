package nl.tim.questplugin.quest.stage.rewards;

import nl.tim.questplugin.quest.stage.Reward;
import org.bukkit.entity.Player;

/**
 * Very simple reward, mainly used for testing. This reward is just a placeholder for sending messages upon
 * completion of a task/stage/quest
 * Setting: {@link String}
 * Behaviour: Send the message set as the setting to the player
 */
public class MessageReward extends Reward
{
    public MessageReward()
    {
        super("msg",
                "msg",
                "User should not see this");
    }

    @Override
    public void giveReward(Player player, Object setting)
    {
        player.sendMessage((String) setting);
    }
}
