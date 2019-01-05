package nl.tim.questplugin.quest.tasks;

import nl.tim.questplugin.quest.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

public class DummyTask extends Task
{
    public DummyTask()
    {
        super(UUID.fromString("d035018a-895d-40e4-9165-0a01c3684797"),
                "dummytask",
                "Dummy Task",
                5);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event)
    {
        incrementScore(null);
    }
}
