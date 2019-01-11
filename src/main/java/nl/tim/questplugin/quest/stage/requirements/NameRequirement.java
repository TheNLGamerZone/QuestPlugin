package nl.tim.questplugin.quest.stage.requirements;

import nl.tim.questplugin.api.ExtensionInformation;
import nl.tim.questplugin.api.InputType;
import nl.tim.questplugin.api.Requirement;
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
