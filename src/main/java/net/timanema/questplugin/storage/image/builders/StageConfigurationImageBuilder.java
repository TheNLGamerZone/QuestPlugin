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

package net.timanema.questplugin.storage.image.builders;

import net.timanema.questplugin.QuestPlugin;
import net.timanema.questplugin.api.Reward;
import net.timanema.questplugin.api.Task;
import net.timanema.questplugin.quest.wrappers.RequirementWrapper;
import net.timanema.questplugin.storage.Storage;
import net.timanema.questplugin.storage.StorageProvider;
import net.timanema.questplugin.utils.StringUtils;
import net.timanema.questplugin.quest.stage.StageConfiguration;
import net.timanema.questplugin.quest.stage.StageOption;
import net.timanema.questplugin.storage.image.ImageBuilder;

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
                <config_option>: <value>
            tasks:
                - <task_uuid>
                - <task_uuid>
            task_rewards:
                - <reward_uuid>
                - <reward_uuid>
            requirements:
                <group_id>:
                    - <requirement_uuid>
                    - <requirement_uuid>
                <group_id>:
                    - <requirement_uuid>
                    - <requirement_uuid>
            stage_start_rewards:
                - <reward_uuid>
                - <reward_uuid>
            stage_finish_rewards:
                - <reward_uuid>
                - <reward_uuid>
         */

        List<Storage.DataPair> savedStageConfiguration = new ArrayList<>();

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

    private void appendStageConfiguration(List<Storage.DataPair> savedStageConfiguration,
                                          Map<StageOption, Object> keys)
    {
        keys.keySet().forEach(option -> savedStageConfiguration.add(
                        new Storage.DataPair<>("stage_config." + option.name(),
                                keys.get(option).toString())));
    }

    private void appendAndSaveStages(List<Storage.DataPair> savedStageConfiguration,
                                     Collection<Task> keys)
    {
        Set<String> ids = new HashSet<>();

        keys.forEach(task -> {
            ids.add(task.getUUID().toString());
            this.questPlugin.getExtensionImageBuilder().save(task);
        });

        savedStageConfiguration.add(new Storage.DataPair<>("tasks", ids));
    }

    private void appendAndSaveRewards(List<Storage.DataPair> savedStageConfiguration,
                                      String rewardType,
                                      Collection<Reward> keys)
    {
        Set<String> ids = new HashSet<>();

        keys.forEach(reward -> {
            ids.add(reward.getUUID().toString());
            this.questPlugin.getExtensionImageBuilder().save(reward);
        });

        savedStageConfiguration.add(new Storage.DataPair<>(rewardType, ids));
    }

    private void appendAndSaveRequirements(List<Storage.DataPair> savedStageConfiguration,
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
        List<Storage.DataPair> dataPairs = this.storage.load(uuid, Storage.DataType.STAGE_CONFIG);

        // Check if the uuid was valid
        if (dataPairs == null || dataPairs.isEmpty())
        {
            QuestPlugin.getLog().warning("Stage configuration could not be loaded: unknown uuid");
            return null;
        }

        // Do some work beforehand
        List<Storage.DataPair<String>> stageConfigurationPairs = new ArrayList<>();
        Storage.DataPair<Collection> taskPairs = null;
        Storage.DataPair<Collection> taskRewardsPairs = null;
        Set<Storage.DataPair<Collection>> requirementPairs = new HashSet<>();
        Storage.DataPair<Collection> stageStartPairs = null;
        Storage.DataPair<Collection> stageRewardPairs = null;

        for (Storage.DataPair dataPair : dataPairs)
        {
            String key = dataPair.getKey();

            if (dataPair.isCollection())
            {
                Storage.DataPair<Collection> collectionPair = new Storage.DataPair<>(key,
                        (Collection) dataPair.getData());

                switch (key)
                {
                    case "tasks":
                        taskPairs = collectionPair;
                        break;
                    case "task_rewards":
                        taskRewardsPairs = collectionPair;
                        break;
                    case "stage_start_rewards":
                        stageStartPairs = collectionPair;
                        break;
                    case "stage_finish_rewards":
                        stageRewardPairs = collectionPair;
                        break;
                    default:
                        if (key.contains("requirements"))
                        {
                            requirementPairs.add(collectionPair);
                        } else
                        {
                            QuestPlugin.getLog().warning(
                                    "Unknown data field found while loading stage configuration: " + key);
                        }
                        break;
                }
            } else
            {
                Storage.DataPair<String> stringPair = new Storage.DataPair<>(key, (String) dataPair.getData());

                if (key.contains("stage_config"))
                {
                    stageConfigurationPairs.add(stringPair);
                } else
                {
                    QuestPlugin.getLog().warning("Unknown data field found while loading stage configuration: " + key);
                }
            }
        }

        Map<StageOption, Object> stageConfiguration = this.loadStageConfig(stageConfigurationPairs);
        Set<Task> tasks = this.loadTasks(taskPairs);
        List<Reward> taskRewards = this.loadReward(taskRewardsPairs);
        RequirementWrapper requirements = this.loadRequirements(requirementPairs);
        List<Reward> stageStart = this.loadReward(stageStartPairs);
        List<Reward> stageRewards = this.loadReward(stageRewardPairs);

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

    private Set<Task> loadTasks(Storage.DataPair<Collection> tasks)
    {
        Set<Task> result = new HashSet<>();

        if (tasks == null)
        {
            return result;
        }

        for (Object rawID : tasks.getData())
        {
            if (StringUtils.isUUID(rawID.toString()))
            {
                UUID taskUUID = UUID.fromString(rawID.toString());

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
                QuestPlugin.getLog().warning("Found illegal task uuid: " + rawID);
            }
        }

        return result;
    }

    private List<Reward> loadReward(Storage.DataPair<Collection> rewards)
    {
        List<Reward> result = new ArrayList<>();

        if (rewards == null)
        {
            return result;
        }

        for (Object rawID : rewards.getData())
        {
            if (StringUtils.isUUID(rawID.toString()))
            {
                UUID uuid = UUID.fromString(rawID.toString());

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
                QuestPlugin.getLog().warning("Found illegal reward uuid: " + rawID);
            }
        }

        return result;
    }

    private RequirementWrapper loadRequirements(Set<Storage.DataPair<Collection>> dataPairs)
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
