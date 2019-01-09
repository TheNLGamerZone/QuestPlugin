package nl.tim.questplugin.quest.stage;

import nl.tim.questplugin.quest.Reward;
import nl.tim.questplugin.quest.Task;
import nl.tim.questplugin.quest.wrappers.RequirementWrapper;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.*;

public class StageConfiguration
{
    /* Can we get a F in the chat for the person (me) who has to write code to save+load all this */
    private Map<StageOption, Object> stageConfiguration;
    private Set<Task> tasks;
    private MultiValuedMap<Task, Reward> taskRewards;
    private List<List<RequirementWrapper>> requirements;
    private List<Reward> stageStart;
    private List<Reward> stageRewards;
    private UUID parentUUID;

    public StageConfiguration(Map<StageOption, Object> stageConfiguration,
                              Set<Task> tasks,
                              MultiValuedMap<Task, Reward> taskRewards,
                              List<List<RequirementWrapper>> requirements,
                              List<Reward> stageStart,
                              List<Reward> stageRewards,
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
    public Set<Task> getTasks()
    {
        return this.tasks;
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
     * Returns a {@link Collection} containing all rewards and their settings for the given {@link Task}.
     * @param task {@link Task} to get rewards for
     * @return A {@link Collection} containing all rewards and their settings (empty if task not found).
     */
    public Collection<Reward> getRewardForTask(Task task)
    {
        return this.taskRewards.get(task);
    }

    public Map<StageOption, Object> getStageConfigurationMap()
    {
        return this.stageConfiguration;
    }

    public MultiValuedMap<Task, Reward> getTaskRewardsMap()
    {
        return this.taskRewards;
    }

    public List<Reward> getStageRewards()
    {
        return this.stageRewards;
    }

    public List<Reward> getStageStartRewards()
    {
        return this.stageStart;
    }

    public UUID getParentUUID()
    {
        return this.parentUUID;
    }
}
