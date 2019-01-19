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

package nl.tim.questplugin.storage.image.builders;

import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.api.Reward;
import nl.tim.questplugin.api.Task;
import nl.tim.questplugin.quest.stage.StageConfiguration;
import nl.tim.questplugin.quest.stage.StageOption;
import nl.tim.questplugin.quest.wrappers.RequirementWrapper;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.StorageProvider;
import nl.tim.questplugin.storage.image.ImageBuilder;
import nl.tim.questplugin.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class StageConfigurationImageBuilder implements ImageBuilder<StageConfiguration>
{
    private QuestPlugin questPlugin;
    private Storage storage;

    @Inject
    public StageConfigurationImageBuilder(QuestPlugin questPlugin, StorageProvider storageProvider)
    {
        this.questPlugin = questPlugin;
        this.storage = storageProvider.getStorage(QuestPlugin.storageType);
    }

    @Override
    public void save(StageConfiguration configuration)
    {
        if (configuration == null)
        {
            return;
        }
        /*
        Format will be like this:
        <parent_uuid>:
            stage_config:
                <config_option>: <value>
                .
                .
            task:
                <task_uuid>: TASK
                .
                .
            task_rewards:
                <reward_uuid>: REWARD
                .
                .
            requirements:
                <group_id>:
                    <requirement_uuid>: REQUIREMENT
                    .
                    .
                 .
                 .
            stage_start_rewards:
                <reward_uuid>: REWARD
                .
                .
            stage_finish_rewards:
                <reward_uuid>: REWARD
                .
                .
         */

        List<Storage.DataPair<String>> savedStageConfiguration = new ArrayList<>();

        // Save stage configuration options
        this.appendStageConfiguration(savedStageConfiguration, configuration.getStageConfigurationMap());

        // Save tasks and a reference to the tasks
        this.appendAndSaveStages(savedStageConfiguration, configuration.getTasks());

        // Save task rewards and reference to rewards
        this.appendAndSaveRewards(savedStageConfiguration, "task_rewards", configuration.getRewards());

        // Save requirements and reference to requirements
        this.appendAndSaveRequirements(savedStageConfiguration, configuration.getRequirements());

        // Save stage start rewards and references
        this.appendAndSaveRewards(savedStageConfiguration, "stage_start_rewards", configuration.getStageStartRewards());

        // Save stage finish rewards and references
        this.appendAndSaveRewards(savedStageConfiguration, "stage_finish_rewards", configuration.getStageRewards());

        // Save data
        this.storage.save(configuration.getParentUUID(), Storage.DataType.STAGE_CONFIG, savedStageConfiguration);
    }

    private void appendStageConfiguration(List<Storage.DataPair<String>> savedStageConfiguration,
                                          Map<StageOption, Object> keys)
    {
        keys.keySet().forEach(option -> savedStageConfiguration.add(
                        new Storage.DataPair<>("stage_config." + option.name(),
                                keys.get(option).toString())));
    }

    private void appendAndSaveStages(List<Storage.DataPair<String>> savedStageConfiguration,
                                     Collection<Task> keys)
    {
        keys.forEach(task -> {
            savedStageConfiguration.add(new Storage.DataPair<>(
                    "task." + task.getUUID(), "TASK"));
            this.questPlugin.getExtensionImageBuilder().save(task);
        });
    }

    private void appendAndSaveRewards(List<Storage.DataPair<String>> savedStageConfiguration,
                                      String rewardType,
                                      Collection<Reward> keys)
    {
        keys.forEach(reward -> {
            savedStageConfiguration.add(new Storage.DataPair<>(
                    rewardType + "." + reward.getUUID(), "REWARD"));
            this.questPlugin.getExtensionImageBuilder().save(reward);
        });
    }

    private void appendAndSaveRequirements(List<Storage.DataPair<String>> savedStageConfiguration,
                                           RequirementWrapper requirements)
    {
        // Append data
        savedStageConfiguration.addAll(requirements.getData());

        // Save all requirements
        requirements.getRequirements().forEach(group ->
                group.forEach(requirement -> this.questPlugin.getExtensionImageBuilder().save(requirement)));
    }

    @Override
    public StageConfiguration load(UUID uuid)
    {
        List<Storage.DataPair<String>> dataPairs = this.storage.load(uuid, Storage.DataType.STAGE_CONFIG);

        // Check if the uuid was valid
        if (dataPairs == null || dataPairs.isEmpty())
        {
            QuestPlugin.getLog().warning("Stage configuration could not be loaded: unknown uuid");
            return null;
        }

        // Do some work beforehand
        List<Storage.DataPair<String>> stageConfigurationPairs = new ArrayList<>();
        List<Storage.DataPair<String>> taskPairs = new ArrayList<>();
        List<Storage.DataPair<String>> taskRewardsPairs = new ArrayList<>();
        List<Storage.DataPair<String>> requirementPairs = new ArrayList<>();
        List<Storage.DataPair<String>> stageStartPairs = new ArrayList<>();
        List<Storage.DataPair<String>> stageRewardPairs = new ArrayList<>();

        for (Storage.DataPair<String> dataPair : dataPairs)
        {
            String key = dataPair.getKey();

            if (key.contains("stage_config"))
            {
                stageConfigurationPairs.add(dataPair);
            } else if (key.contains("task"))
            {
                taskPairs.add(dataPair);
            } else if (key.contains("task_rewards"))
            {
                taskRewardsPairs.add(dataPair);
            } else if (key.contains("requirements"))
            {
                requirementPairs.add(dataPair);
            } else if (key.contains("stage_start_rewards"))
            {
                stageStartPairs.add(dataPair);
            } else if (key.contains("stage_finish_rewards"))
            {
                stageRewardPairs.add(dataPair);
            } else
            {
                QuestPlugin.getLog().warning("Unknown data field found while loading stage configuration: " + key);
            }
        }

        Map<StageOption, Object> stageConfiguration = this.loadStageConfig(stageConfigurationPairs);
        Set<Task> tasks = this.loadTasks(taskPairs);
        List<Reward> taskRewards = this.loadReward(taskRewardsPairs, "task_rewards");
        RequirementWrapper requirements = this.loadRequirements(requirementPairs);
        List<Reward> stageStart = this.loadReward(stageStartPairs, "stage_start_rewards");
        List<Reward> stageRewards = this.loadReward(stageRewardPairs, "stage_finish_rewards");

        // Check if anything failed to load
        if (stageConfiguration == null ||
                tasks == null ||
                taskRewards == null ||
                requirements == null ||
                stageStart == null ||
                stageRewards == null)
        {
            QuestPlugin.getLog().warning("Stage configuration could not be loaded!");
            return null;
        }

        // Build config
        return new StageConfiguration(stageConfiguration,
                tasks, taskRewards, requirements, stageStart, stageRewards, uuid);
    }

    private StageOption getStageOption(String option)
    {
        for (StageOption stageOption : StageOption.values())
        {
            if (stageOption.name().equalsIgnoreCase(option))
            {
                return stageOption;
            }
        }

        return null;
    }

    private Map<StageOption, Object> loadStageConfig(List<Storage.DataPair<String>> dataPairs)
    {
        Map<StageOption, Object> result = new HashMap<>();

        for (Storage.DataPair dataPair : dataPairs)
        {
            String rawOption = StringUtils.stripIncluding(dataPair.getKey(), "stage_config", true);
            StageOption option = this.getStageOption(rawOption);

            // Check if option was valid
            if (option == null)
            {
                QuestPlugin.getLog().warning("Could not determine stage option '" + rawOption+ "', ignoring");
                continue;
            }

            Object value = option.isToggle() ? Boolean.valueOf(dataPair.getData().toString()) : dataPair.getData();

            result.put(option, value);
        }

        return result;
    }

    private Set<Task> loadTasks(List<Storage.DataPair<String>> dataPairs)
    {
        Set<Task> result = new HashSet<>();

        for (Storage.DataPair dataPair : dataPairs)
        {
            String rawUUID = StringUtils.stripIncluding(dataPair.getKey(), "task", true);

            if (StringUtils.isUUID(rawUUID))
            {
                UUID taskUUID = UUID.fromString(rawUUID);

                // Load task
                Task task = (Task) this.questPlugin.getExtensionImageBuilder().load(taskUUID);

                // Check if task failed to load
                if (task == null)
                {
                    QuestPlugin.getLog().warning("Tasks failed to load!");
                    return null;
                }

                // Add task to set
                result.add(task);
            } else
            {
                QuestPlugin.getLog().warning("Found illegal task uuid: " + rawUUID);
            }
        }

        return result;
    }

    private List<Reward> loadReward(List<Storage.DataPair<String>> dataPairs, String type)
    {
        List<Reward> result = new ArrayList<>(dataPairs.size());

        for (Storage.DataPair dataPair : dataPairs)
        {
            String rawUUID = StringUtils.stripIncluding(dataPair.getKey(), type, true);

            if (StringUtils.isUUID(rawUUID))
            {
                UUID uuid = UUID.fromString(rawUUID);

                // Load reward
                Reward reward = (Reward) this.questPlugin.getExtensionImageBuilder().load(uuid);

                // Check if reward failed to load
                if (reward == null)
                {
                    QuestPlugin.getLog().warning("Rewards failed to load!");
                    return null;
                }

                // Add reward to list
                result.add(reward);
            } else
            {
                QuestPlugin.getLog().warning("Found illegal reward uuid: " + rawUUID);
            }
        }

        return result;
    }

    private RequirementWrapper loadRequirements(List<Storage.DataPair<String>> dataPairs)
    {
        RequirementWrapper result = RequirementWrapper.load(this.questPlugin, dataPairs);

        // Check if something went wrong
        if (result == null)
        {
            QuestPlugin.getLog().warning("Requirements failed to load!");
            return null;
        }

        return result;
    }
}
