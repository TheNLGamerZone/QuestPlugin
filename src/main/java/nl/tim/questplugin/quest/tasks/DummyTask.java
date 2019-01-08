package nl.tim.questplugin.quest.tasks;

import nl.tim.questplugin.quest.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Set;

public class DummyTask extends Task
{
    public DummyTask()
    {
        super("dummytask","Dummy Task");
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event)
    {
        //
    }

    @Override
    public Set<TaskOption> getRequiredConfiguration()
    {
        return null;
    }

    @Override
    public TaskOption getFinishOption()
    {
        return null;
    }
}
