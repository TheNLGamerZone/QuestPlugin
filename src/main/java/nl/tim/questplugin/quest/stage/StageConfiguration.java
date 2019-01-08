package nl.tim.questplugin.quest.stage;

import nl.tim.questplugin.quest.Task;
import nl.tim.questplugin.quest.wrappers.RequirementWrapper;
import nl.tim.questplugin.quest.wrappers.RewardWrapper;
import nl.tim.questplugin.quest.wrappers.TaskWrapper;
import nl.tim.questplugin.quest.tasks.TaskOption;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.*;

public class StageConfiguration
{
    /* Can we get a F in the chat for the person (me) who has to write code to save+load all this */
    private Map<StageOption, Object> stageConfiguration;
    private Set<TaskWrapper> tasks;
    private MultiValuedMap<TaskWrapper, RewardWrapper> taskRewards;
    private List<List<RequirementWrapper>> requirements;
    private List<RewardWrapper> stageStart;
    private List<RewardWrapper> stageRewards;
    private UUID parentUUID;

    public StageConfiguration(Map<StageOption, Object> stageConfiguration,
                              Set<TaskWrapper> tasks,
                              MultiValuedMap<TaskWrapper, RewardWrapper> taskRewards,
                              List<List<RequirementWrapper>> requirements,
                              List<RewardWrapper> stageStart,
                              List<RewardWrapper> stageRewards,
                              UUID parentUUID)
    {
        this.stageConfiguration = stageConfiguration;
        this.tasks = tasks;
        this.taskRewards = taskRewards;
        this.requirements = requirements;
        this.stageStart = stageStart;
        this.stageRewards = stageRewards;
        this.parentUUID = parentUUID;
    }

    /**
     * Returns the value set for the given {@link StageOption}.
     * @param stageOption {@link StageOption} to check
     * @return Object representing the value that was set.
     */
    public Object getOption(StageOption stageOption)
    {
        return this.stageConfiguration.getOrDefault(stageOption, null);
    }

    /**
     * Returns a {@link Set<Task>} containing all tasks that were configured for this stage.
     * @return A {@link Set<Task>} containing all tasks that were configured for this stage.
     */
    public Set<TaskWrapper> getTaskWrappers()
    {
        return this.tasks;
    }

    /**
     * Returns a list containing all the configurations for tasks that were set.
     * @param task {@link Task} to check
     * @return A list with all configurations, null if task not found.
     */
    public List<Map<TaskOption, Object>> getTaskConfigurations(Task task)
    {
        List<Map<TaskOption, Object>> result = new ArrayList<>();

        for (TaskWrapper wrapper : this.tasks)
        {
            if (task.equals(wrapper.getTask()))
            {
                result.add(wrapper.getTaskConfiguration());
            }
        }

        return result;
    }

    /**
     * Returns a {@link List} of {@link List}s. All {@link Requirement}s in a {@link List}
     * form a group of requirements that have an OR relation (i.e. when at least one requirement in a group is met,
     * the whole group will be counted as 'requirement met'), while each all {@link List} have a AND relation
     * (i.e. all maps in the list have to be marked as 'requirement met' in order for the stage requirements to be met).
     * @return A {@link List} of {@link List}s.
     */
    public List<List<RequirementWrapper>> getRequirements()
    {
        return this.requirements;
    }

    /**
     * Returns a {@link Collection} containing all rewards and their settings for the given {@link TaskWrapper}.
     * @param wrapper {@link TaskWrapper} to get rewards for
     * @return A {@link Collection} containing all rewards and their settings (empty if task not found).
     */
    public Collection<RewardWrapper> getRewardForTask(TaskWrapper wrapper)
    {
        return this.taskRewards.get(wrapper);
    }

    public Map<StageOption, Object> getStageConfigurationMap()
    {
        return this.stageConfiguration;
    }

    public MultiValuedMap<TaskWrapper, RewardWrapper> getTaskRewardsMap()
    {
        return this.taskRewards;
    }

    public List<RewardWrapper> getStageRewards()
    {
        return this.stageRewards;
    }

    public List<RewardWrapper> getStageStartRewards()
    {
        return this.stageStart;
    }

    public UUID getParentUUID()
    {
        return this.parentUUID;
    }
}
