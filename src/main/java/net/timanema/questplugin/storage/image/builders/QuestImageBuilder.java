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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.timanema.questplugin.QuestPlugin;
import net.timanema.questplugin.api.Reward;
import net.timanema.questplugin.api.Trigger;
import net.timanema.questplugin.quest.wrappers.RequirementWrapper;
import net.timanema.questplugin.storage.Storage;
import net.timanema.questplugin.storage.StorageProvider;
import net.timanema.questplugin.utils.StringUtils;
import net.timanema.questplugin.area.Area;
import net.timanema.questplugin.quest.CustomExtension;
import net.timanema.questplugin.quest.Quest;
import net.timanema.questplugin.quest.stage.Stage;
import net.timanema.questplugin.storage.image.ImageBuilder;
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
            hidden: <boolean>
            stages:
                - <stage_uuid>
                - <stage_uuid>
            rewards:
                - <reward_uuid>
                - <reward_uuid>
            triggers:
                - <trigger_uuid>
                - <trigger_uuid>
            requirements:
                <group_id>:
                    - <requirement_uuid>
                    - <requirement_uuid>
                <group_id>:
                    - <requirement_uuid>
                    - <requirement_uuid>
         */
        List<Storage.DataPair> result = new ArrayList<>();

        // Add flags
        result.add(new Storage.DataPair<>("area", quest.getQuestArea().getUUID().toString()));
        result.add(new Storage.DataPair<>("area_locked", quest.isAreaLocked() + ""));
        result.add(new Storage.DataPair<>("list_progress", "false")); //TODO: Implement
        result.add(new Storage.DataPair<>("replayable", quest.isReplayable() + ""));
        result.add(new Storage.DataPair<>("sequential", quest.isSequential() + ""));
        result.add(new Storage.DataPair<>("hidden", quest.isHidden() + ""));

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

    private void appendAndSaveStages(List<Storage.DataPair> result, Collection<Stage> stages)
    {
        Set<String> ids = new HashSet<>();

        stages.forEach(stage -> {
            ids.add(stage.getUUID().toString());
            this.questPlugin.getStageImageBuilder().save(stage);
        });

        result.add(new Storage.DataPair<>("stages", ids));
    }

    private void appendAndSaveTriggers(List<Storage.DataPair> result, Collection<Trigger> triggers)
    {
        Set<String> ids = new HashSet<>();

        triggers.forEach(trigger -> {
            ids.add(trigger.getUUID().toString());
            this.questPlugin.getExtensionImageBuilder().save(trigger);
        });

        result.add(new Storage.DataPair<>("triggers", ids));
    }

    private void appendAndSaveRewards(List<Storage.DataPair> result,
                                      Collection<Reward> keys)
    {
        Set<String> ids = new HashSet<>();

        keys.forEach(reward -> {
            ids.add(reward.getUUID().toString());
            this.questPlugin.getExtensionImageBuilder().save(reward);
        });

        result.add(new Storage.DataPair<>("rewards", ids));
    }

    private void appendAndSaveRequirements(List<Storage.DataPair> result,
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
        List<Storage.DataPair> dataPairs = this.storage.load(uuid, Storage.DataType.QUEST);

        // Check if uuid was valid
        if (dataPairs == null)
        {
            return null;
        }

        // Do some work beforehand
        Storage.DataPair<Collection> stagePairs = null;
        Storage.DataPair<Collection> rewardPairs = null;
        Storage.DataPair<Collection> triggerPairs = null;
        Set<Storage.DataPair<Collection>> requirementPairs = new HashSet<>();

        Area area = null;
        Boolean areaLocked = null;
        Boolean listProgress = null;
        Boolean replayable = null;
        Boolean sequential = null;
        Boolean hidden = null;

        // Loop over pairs
        for (Storage.DataPair dataPair : dataPairs)
        {
            String key = dataPair.getKey();

            // Check if this is a collection or not
            if (dataPair.isCollection())
            {
                Storage.DataPair<Collection> collectionPair = new Storage.DataPair<>(key,
                        (Collection) dataPair.getData());


                switch (key)
                {
                    case "stages":
                        stagePairs = collectionPair;
                        break;
                    case "rewards":
                        rewardPairs = collectionPair;
                        break;
                    case "triggers":
                        triggerPairs = collectionPair;
                        break;
                    default:
                        if (key.contains("requirements"))
                        {
                            requirementPairs.add(collectionPair);
                        } else
                        {
                            QuestPlugin.getLog().warning("Unknown data field found while loading stage configuration: " + key);
                        }
                        break;
                }
            } else
            {
                Storage.DataPair<String> stringPair = new Storage.DataPair<>(key, (String) dataPair.getData());
                String data = stringPair.getData();

                switch (key)
                {
                    case "area":
                        if (StringUtils.isUUID(data))
                        {
                            area = this.questPlugin.getAreaImageBuilder().load(UUID.fromString(data));
                        }
                        break;
                    case "area_locked":
                        areaLocked = BooleanUtils.toBooleanObject(data);
                        break;
                    case "list_progress":
                        listProgress = BooleanUtils.toBooleanObject(data);
                        break;
                    case "replayable":
                        replayable = BooleanUtils.toBooleanObject(data);
                        break;
                    case "sequential":
                        sequential = BooleanUtils.toBooleanObject(data);
                        break;
                    case "hidden":
                        hidden = BooleanUtils.toBooleanObject(data);
                        break;
                    default:
                        QuestPlugin.getLog().warning("Unknown data field found while loading stage configuration: " + key);
                        break;
                }
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

    private LinkedList<Stage> loadStages(Storage.DataPair<Collection> dataPairs)
    {
        LinkedList<Stage> stages = new LinkedList<>();

        if (dataPairs == null)
        {
            return stages;
        }

        for (Object rawID : dataPairs.getData())
        {
            if (StringUtils.isUUID(rawID))
            {
                Stage stage = this.questPlugin.getStageImageBuilder().load(UUID.fromString(rawID.toString()));

                // Check if stage failed to load
                if (stage == null)
                {
                    QuestPlugin.getLog().warning("Could not load stage '" + rawID + "'");
                    return null;
                }

                stages.add(stage);
            }
        }

        return stages;
    }

    private Set<Reward> loadRewards(Storage.DataPair<Collection> dataPairs)
    {
        Set<Reward> rewards = new HashSet<>();

        if (dataPairs == null)
        {
            return rewards;
        }

        for (Object rawID : dataPairs.getData())
        {
            if (StringUtils.isUUID(rawID))
            {
                Reward reward = (Reward) this.questPlugin.getExtensionImageBuilder().load(UUID.fromString(rawID.toString()));

                // Check if reward failed to load
                if (reward == null)
                {
                    QuestPlugin.getLog().warning("Could not load reward '" + rawID + "'");
                    return null;
                }

                rewards.add(reward);
            }
        }

        return rewards;
    }

    private Set<Trigger> loadTriggers(Storage.DataPair<Collection> dataPairs)
    {
        Set<Trigger> triggers = new HashSet<>();

        if (dataPairs == null)
        {
            return triggers;
        }

        for (Object rawID : dataPairs.getData())
        {
            if (StringUtils.isUUID(rawID))
            {
                Trigger trigger = (Trigger) this.questPlugin.getExtensionImageBuilder().load(UUID.fromString(rawID.toString()));

                // Check if trigger failed to load
                if (trigger == null)
                {
                    QuestPlugin.getLog().warning("Could not load trigger '" + rawID + "'");
                    return null;
                }

                triggers.add(trigger);
            }
        }

        return triggers;
    }
}
