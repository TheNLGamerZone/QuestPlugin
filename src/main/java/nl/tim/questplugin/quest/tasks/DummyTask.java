package nl.tim.questplugin.quest.tasks;

import nl.tim.questplugin.quest.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

@TaskInformation(identifier = "dummytask", author = "Tim")
public class DummyTask extends Task
{
    public DummyTask()
    {
        super("Dummy TaskInformation");
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event)
    {
        //
    }

    @Override
    public Map<String, String> getRequiredConfiguration()
    {
        return null;
    }

    @Override
    public Integer getRequiredProgressToFinish()
    {
        return null;
    }
}
