package nl.tim.questplugin.quest.stage.requirements;

import nl.tim.questplugin.api.ExtensionInformation;
import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.quest.stage.Requirement;
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

        this.addConfiguration("name", "Player name will be compared to this");
    }

    @Override
    public boolean checkRequirement(QPlayer qPlayer, Player player)
    {
        String requiredName = (String) this.getSetting("name");

        return player.getDisplayName().equals(requiredName);
    }
}
