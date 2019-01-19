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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.api.Reward;
import nl.tim.questplugin.api.Trigger;
import nl.tim.questplugin.area.Area;
import nl.tim.questplugin.quest.CustomExtension;
import nl.tim.questplugin.quest.Quest;
import nl.tim.questplugin.quest.stage.Stage;
import nl.tim.questplugin.quest.wrappers.RequirementWrapper;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.StorageProvider;
import nl.tim.questplugin.storage.image.ImageBuilder;
import nl.tim.questplugin.utils.StringUtils;
import org.apache.commons.lang.BooleanUtils;

import java.util.*;

@Singleton
public class QuestImageBuilder implements ImageBuilder<Quest>
{
    private QuestPlugin questPlugin;
    private Storage storage;

    @Inject
    public QuestImageBuilder(QuestPlugin questPlugin, StorageProvider storageProvider)
    {
        this.questPlugin = questPlugin;
        this.storage = storageProvider.getStorage(QuestPlugin.storageType);
    }

    @Override
    public void save(Quest quest)
    {
        if (quest == null)
        {
            return;
        }

        /*
        Format will be like this:
        <uuid>:
            area: <area_uuid>
            area_locked: <boolean>
            list_progress: <boolean>
            replayable: <boolean>
            sequential: <boolean>
            stages:
                <stage_uuid>: STAGE
                .
            rewards:
                <reward_uuid>: REWARD
                .
            triggers:
                <trigger_uuid>: TRIGGER
                .
            requirements:
                <group_id>:
                    <requirement_uuid>: REQUIREMENT
                    .
                 .
         */
        List<Storage.DataPair<String>> result = new ArrayList<>();

        // Add flags
        result.add(new Storage.DataPair<>("area", quest.getQuestArea().getUUID().toString()));
        result.add(new Storage.DataPair<>("area_locked", quest.isAreaLocked() + ""));
        result.add(new Storage.DataPair<>("list_progress", "false")); //TODO: Implement
        result.add(new Storage.DataPair<>("replayable", quest.isReplayable() + ""));
        result.add(new Storage.DataPair<>("sequential", quest.isSequential() + ""));

        // Append stages
        this.appendAndSaveStages(result, quest.getStages());

        // Append rewards
        this.appendAndSaveRewards(result, quest.getRewards());

        // Append triggers
        this.appendAndSaveTriggers(result, quest.getTriggers());

        // Append requirements
        this.appendAndSaveRequirements(result, quest.getRequirements());

        // Save quest
        this.storage.save(quest.getUUID(), Storage.DataType.QUEST, result);
    }

    private void appendAndSaveStages(List<Storage.DataPair<String>> result, Collection<Stage> stages)
    {
        stages.forEach(stage -> {
            result.add(new Storage.DataPair<>("stages." + stage.getUUID(), "STAGE"));
            this.questPlugin.getStageImageBuilder().save(stage);
        });
    }

    private void appendAndSaveTriggers(List<Storage.DataPair<String>> result, Collection<Trigger> triggers)
    {
        triggers.forEach(trigger -> {
            result.add(new Storage.DataPair<>("triggers." + trigger.getUUID(), "TRIGGER"));
            this.questPlugin.getExtensionImageBuilder().save(trigger);
        });
    }

    private void appendAndSaveRewards(List<Storage.DataPair<String>> result,
                                      Collection<Reward> keys)
    {
        keys.forEach(reward -> {
            result.add(new Storage.DataPair<>(
                    "rewards." + reward.getUUID(), "REWARD"));
            this.questPlugin.getExtensionImageBuilder().save(reward);
        });
    }

    private void appendAndSaveRequirements(List<Storage.DataPair<String>> result,
                                           RequirementWrapper requirements)
    {
        // Append data
        result.addAll(requirements.getData());

        // Save all requirements
        requirements.getRequirements().forEach(group ->
                group.forEach(requirement -> this.questPlugin.getExtensionImageBuilder().save(requirement)));
    }

    @Override
    public Quest load(UUID uuid)
    {
        List<Storage.DataPair<String>> dataPairs = this.storage.load(uuid, Storage.DataType.QUEST);

        // Check if uuid was valid
        if (dataPairs == null)
        {
            return null;
        }

        // Do some work beforehand
        List<Storage.DataPair<String>> stagePairs = new ArrayList<>();
        List<Storage.DataPair<String>> rewardPairs = new ArrayList<>();
        List<Storage.DataPair<String>> triggerPairs = new ArrayList<>();
        List<Storage.DataPair<String>> requirementPairs = new ArrayList<>();

        Area area = null;
        Boolean areaLocked = null;
        Boolean listProgress = null;
        Boolean replayable = null;
        Boolean sequential = null;
        Boolean hidden = null;

        // Loop over pairs
        for (Storage.DataPair<String> dataPair : dataPairs)
        {
            String key = dataPair.getKey();
            String data = dataPair.getData();

            if (key.contains("stages"))
            {
                stagePairs.add(dataPair);
            } else if (key.contains("rewards"))
            {
                rewardPairs.add(dataPair);
            } else if (key.contains("triggers"))
            {
                triggerPairs.add(dataPair);
            } else if (key.contains("requirements"))
            {
                requirementPairs.add(dataPair);
            } else if (key.contains("area_locked"))
            {
                areaLocked = BooleanUtils.toBooleanObject(data);
            } else if (key.contains("area"))
            {
                if (StringUtils.isUUID(data))
                {
                    area = this.questPlugin.getAreaImageBuilder().load(UUID.fromString(data));
                }
            } else if (key.contains("list_progress"))
            {
                listProgress = BooleanUtils.toBooleanObject(data);
            } else if (key.contains("replayable"))
            {
                replayable = BooleanUtils.toBooleanObject(data);
            } else if (key.contains("sequential"))
            {
                sequential = BooleanUtils.toBooleanObject(data);
            } else if (key.contains("hidden"))
            {
                hidden = BooleanUtils.toBooleanObject(data);
            } else
            {
                QuestPlugin.getLog().warning("Unknown data field found while loading stage configuration: " + key);
            }
        }

        // Parse data pairs
        LinkedList<Stage> stages = this.loadStages(stagePairs);
        Set<Reward> rewards = this.loadRewards(rewardPairs);
        Set<Trigger> triggers = this.loadTriggers(triggerPairs);
        RequirementWrapper requirementWrapper = RequirementWrapper.load(this.questPlugin, requirementPairs);

        // Check if something was not loaded
        if (area == null || areaLocked == null || listProgress == null || replayable == null || sequential == null ||
                hidden == null || stages == null || rewards == null || triggers == null || requirementWrapper == null)
        {
            QuestPlugin.getLog().warning("Not all data for quest '" + uuid + "' could be loaded!");
            return null;
        }

        // Init quest
        Quest quest = new Quest(uuid,
                area,
                stages,
                rewards,
                triggers,
                requirementWrapper,
                areaLocked,
                replayable,
                hidden,
                false,
                sequential,
                false);

        // Create list of extensions
        List<CustomExtension> extensions = new ArrayList<>(quest.getRewards());

        extensions.addAll(quest.getTriggers());

        // Register quest as owner
        extensions.forEach(ext -> ext.registerOwner(quest));
        quest.getRequirements().registerOwner(quest);

        // Do some flag checks
        quest.checkBranches();
        quest.checkBroken();

        return quest;
    }

    private LinkedList<Stage> loadStages(Collection<Storage.DataPair<String>> dataPairs)
    {
        LinkedList<Stage> stages = new LinkedList<>();

        for (Storage.DataPair<String> dataPair : dataPairs)
        {
            String rawUUID = StringUtils.stripIncluding(dataPair.getKey(), "stages", true);

            if (StringUtils.isUUID(rawUUID))
            {
                Stage stage = this.questPlugin.getStageImageBuilder().load(UUID.fromString(rawUUID));

                // Check if stage failed to load
                if (stage == null)
                {
                    QuestPlugin.getLog().warning("Could not load stage '" + rawUUID + "'");
                    return null;
                }

                stages.add(stage);
            }
        }

        return stages;
    }

    private Set<Reward> loadRewards(Collection<Storage.DataPair<String>> dataPairs)
    {
        Set<Reward> rewards = new HashSet<>();

        for (Storage.DataPair<String> dataPair : dataPairs)
        {
            String rawUUID = StringUtils.stripIncluding(dataPair.getKey(), "rewards", true);

            if (StringUtils.isUUID(rawUUID))
            {
                Reward reward = (Reward) this.questPlugin.getExtensionImageBuilder().load(UUID.fromString(rawUUID));

                // Check if reward failed to load
                if (reward == null)
                {
                    QuestPlugin.getLog().warning("Could not load reward '" + rawUUID + "'");
                    return null;
                }

                rewards.add(reward);
            }
        }

        return rewards;
    }

    private Set<Trigger> loadTriggers(Collection<Storage.DataPair<String>> dataPairs)
    {
        Set<Trigger> triggers = new HashSet<>();

        for (Storage.DataPair<String> dataPair : dataPairs)
        {
            String rawUUID = StringUtils.stripIncluding(dataPair.getKey(), "triggers", true);

            if (StringUtils.isUUID(rawUUID))
            {
                Trigger trigger = (Trigger) this.questPlugin.getExtensionImageBuilder().load(UUID.fromString(rawUUID));

                // Check if trigger failed to load
                if (trigger == null)
                {
                    QuestPlugin.getLog().warning("Could not load trigger '" + rawUUID + "'");
                    return null;
                }

                triggers.add(trigger);
            }
        }

        return triggers;
    }
}
