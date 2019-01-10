package nl.tim.questplugin.quest.triggers;

import nl.tim.questplugin.api.ExtensionInformation;
import nl.tim.questplugin.quest.Trigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

@ExtensionInformation(identifier = "trigger_area", author = "Tim")
public class AreaTrigger extends Trigger
{
    public AreaTrigger()
    {
        super("Area trigger", "Will trigger the quest when the player enters" +
                "the defined area");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {

    }
}
