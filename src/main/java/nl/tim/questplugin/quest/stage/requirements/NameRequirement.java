package nl.tim.questplugin.quest.stage.requirements;

import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.quest.stage.Requirement;
import nl.tim.questplugin.quest.stage.Stage;
import org.bukkit.entity.Player;

/**
 * Very simple requirement, mainly used for testing.
 * Setting: {@link String}
 * Behaviour: Returns true if the player display name is equal to the given setting
 */
public class NameRequirement extends Requirement
{
    public NameRequirement()
    {
        super("name", "Player name", "Player name should be equal to the setting");
    }

    @Override
    public boolean checkRequirement(QPlayer qPlayer, Player player, Stage stage, Object setting)
    {
        String requiredName = (String) setting;

        return player.getDisplayName().equals(requiredName);
    }
}
