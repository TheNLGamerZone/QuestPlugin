package nl.tim.questplugin.quest.wrappers;

import nl.tim.questplugin.quest.Task;
import nl.tim.questplugin.quest.tasks.TaskOption;
import nl.tim.questplugin.storage.Saveable;
import nl.tim.questplugin.storage.Storage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

public class TaskWrapper implements Saveable
{
    private UUID uuid;
    private UUID stageUUID;
    private Task task;
    private HashMap<TaskOption, Object> taskConfiguration;

    public TaskWrapper(UUID uuid, UUID stageUUID, Task task, HashMap<TaskOption, Object> taskConfiguration)
    {
        this.uuid = uuid;
        this.stageUUID = stageUUID;
        this.task = task;
        this.taskConfiguration = taskConfiguration;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public UUID getStageUUID()
    {
        return this.stageUUID;
    }

    public Task getTask()
    {
        return this.task;
    }

    public HashMap<TaskOption, Object> getTaskConfiguration()
    {
        return this.taskConfiguration;
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add task identifier
        data.add(new Storage.DataPair<>(uuid + ".task", this.task.getIdentifier()));
        data.add(new Storage.DataPair<>(uuid + ".stage", this.stageUUID.toString()));

        // Add configuration
        for (TaskOption taskOption : this.taskConfiguration.keySet())
        {
            data.add(new Storage.DataPair<>(uuid + ".configuration." + taskOption.name(), this.taskConfiguration.toString()));
        }

        return data;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (object == null || getClass() != object.getClass())
        {
            return false;
        }

        TaskWrapper wrapper = (TaskWrapper) object;

        return new EqualsBuilder()
                .append(uuid, wrapper.uuid)
                .append(stageUUID, wrapper.stageUUID)
                .append(task, wrapper.task)
                .append(taskConfiguration, wrapper.taskConfiguration)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(uuid)
                .append(stageUUID)
                .append(task)
                .append(taskConfiguration)
                .toHashCode();
    }
}
