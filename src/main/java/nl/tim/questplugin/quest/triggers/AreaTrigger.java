package nl.tim.questplugin.quest.triggers;

import nl.tim.questplugin.quest.Trigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class AreaTrigger extends Trigger
{
    public AreaTrigger()
    {
        super("area_trigger", "Area trigger", "Will trigger the quest when the player enters" +
                "the defined area");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {

    }
}
