/*
 * Copyright (C) 2019  Tim Anema
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package nl.tim.questplugin.quest.stage;

import nl.tim.questplugin.api.Requirement;
import nl.tim.questplugin.api.Reward;
import nl.tim.questplugin.api.Task;

import java.util.*;
import java.util.stream.Collectors;

public class StageConfiguration
{
    /* Can we get a F in the chat for the person (me) who has to write code to save+load all this */
    private Map<StageOption, Object> stageConfiguration;
    private Set<Task> tasks;
    private List<Reward> taskRewards;
    private List<List<Requirement>> requirements;
    private List<Reward> stageStart;
    private List<Reward> stageRewards;
    private UUID parentUUID;

    public StageConfiguration(Map<StageOption, Object> stageConfiguration,
                              Set<Task> tasks,
                              List<Reward> taskRewards,
                              List<List<Requirement>> requirements,
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
     * (i.e. all maps in the list have to be marked as 'requirement met' in order for the stage requirements to be met) aka CNF.
     * @return A {@link List} of {@link List}s.
     */
    public List<List<Requirement>> getRequirements()
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
        return this.taskRewards.stream().filter(reward -> reward.getParentUUID().equals(task.getUUID())).collect(
                Collectors.toList());
    }

    public Map<StageOption, Object> getStageConfigurationMap()
    {
        return this.stageConfiguration;
    }

    public List<Reward> getRewards()
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
