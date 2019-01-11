package nl.tim.questplugin.quest.tasks;

import nl.tim.questplugin.api.ExtensionInformation;
import nl.tim.questplugin.api.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

@ExtensionInformation(identifier = "dummytask", author = "Tim")
public class DummyTask extends Task
{
    public DummyTask()
    {
        super("Dummy Task", "A very cool dummy task");
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event)
    {
        //
    }

    @Override
    public Integer getRequiredProgressToFinish()
    {
        return null;
    }
}
