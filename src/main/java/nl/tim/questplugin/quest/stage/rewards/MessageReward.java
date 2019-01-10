package nl.tim.questplugin.quest.stage.rewards;

import nl.tim.questplugin.api.ExtensionInformation;
import nl.tim.questplugin.quest.Reward;
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
    public void giveReward(Player player)
    {
        player.sendMessage(this.getSetting("SEND_MESSAGE_CONTENT").toString()); //TODO: Add enum for these identifiers
    }
}
