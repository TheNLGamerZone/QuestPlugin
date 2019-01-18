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

package nl.tim.questplugin.quest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.api.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Singleton
public class TaskHandler
{
    private Map<String, Class<? extends CustomExtension>> baseTasks;
    private Map<String, Class<? extends CustomExtension>> baseRewards;
    private Map<String, Class<? extends CustomExtension>> baseRequirements;

    private Set<Task> tasks;
    private Set<Requirement> requirements;
    private Set<Reward> rewards;

    private QuestPlugin questPlugin;

    @Inject
    public TaskHandler(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
        this.baseTasks = new HashMap<>();
        this.baseRewards = new HashMap<>();
        this.baseRequirements = new HashMap<>();

        this.tasks = new HashSet<>();
        this.requirements = new HashSet<>();
        this.rewards = new HashSet<>();
    }

    public boolean registerCustomExtension(Class<? extends CustomExtension> extension)
    {
        Class<?> superClazz = extension.getSuperclass();

        // Check if extension has annotation or not parent (idk how the latter would happen but ok)
        if (superClazz == null || !extension.isAnnotationPresent(ExtensionInformation.class))
        {
            QuestPlugin.getLog().warning("Trying to register extension '" + extension.getSimpleName() + "', " +
                    "but it does not contain the needed information!");
            return false;
        }

        // Get needed information
        ExtensionInformation extensionInformation = extension.getAnnotation(ExtensionInformation.class);
        String identifier = extensionInformation.identifier();

        QuestPlugin.getLog().info("Registered extension '" + extension.getSimpleName() + "' by " + extensionInformation.author());


        // Check super class
        if (Task.class.isAssignableFrom(extension) && extension != Task.class)
        {
            this.baseTasks.put(identifier, extension);
        } else if (Requirement.class.isAssignableFrom(extension) && extension != Requirement.class)
        {
            this.baseRequirements.put(identifier, extension);
        } else if (Reward.class.isAssignableFrom(extension) && extension != Reward.class)
        {
            this.baseRewards.put(identifier, extension);
        } else if (Trigger.class.isAssignableFrom(extension) && extension != Trigger.class)
        {
            return this.questPlugin.getQuestHandler().registerQuestTrigger(extension, identifier);
        } else
        {
            QuestPlugin.getLog().warning("Trying to register '" + extension.getSimpleName() + "', but cannot " +
                    "determine type (" + superClazz.getSimpleName() + ")or extended directly from CustomExtension.");
            return false;
        }

        //TODO: Trigger initialization of quests that depended on this task

        return true;
    }

    private Map<String, Class<? extends CustomExtension>> getMap(Class<? extends CustomExtension> type)
    {
        if (type == Task.class)
        {
            return this.baseTasks;
        } else if (type == Requirement.class)
        {
            return this.baseRequirements;
        } else if (type == Reward.class)
        {
            return this.baseRewards;
        } else if (type == Trigger.class)
        {
            return this.questPlugin.getQuestHandler().getBasicTriggers();
        }

        return null;
    }

    public CustomExtension buildExtension(Class<? extends CustomExtension> type,
                                          String identifier,
                                          UUID uuid,
                                          UUID ownerUUID,
                                          Map<String, Object> configuration)
    {
        Map<String, Class<? extends CustomExtension>> map = this.getMap(type);

        // Check if map was found
        if (map == null)
        {
            QuestPlugin.getLog().severe("Could not search base map for extension type '" + type.getSimpleName() + "'");
            return null;
        }

        Class<? extends CustomExtension> extensionClazz = map.get(identifier);

        // Check if extension was found
        if (extensionClazz == null)
        {
            // This is probably some third party extension that is not yet available, will return null so the quest gets marked as
            // broken. In case the extension is registered by another plugin it will be built later anyway
            QuestPlugin.getLog().warning("Could not find extension id, maybe this is a third-party extension? " +
                    "Will be loaded later if that is the case");
            return null;
        }

        CustomExtension extension;

        // Try to get an instance
        try
        {
            extension = extensionClazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            QuestPlugin.getLog().severe("An error occurred while building a extension: ");
            e.printStackTrace();

            return null;
        }

        // Get owner
        Owner owner = null;

        // First check if the owner is a quest or stage
        owner = this.questPlugin.getQuestHandler().getQuestByUUID(ownerUUID);

        if (owner == null)
        {
            owner = this.questPlugin.getQuestHandler().getStage(ownerUUID);
        }

        // Finally check for task
        if (owner == null)
        {
            owner = this.getTask(ownerUUID);
        }

        // Check if owner was not found
        if (owner == null)
        {
            QuestPlugin.getLog().warning("Owner with uuid '" + ownerUUID + "' was not found " +
                    "for " + type.getSimpleName() + " (" + identifier + ")");
            return null;
        }

        // Register the basic stuff
        extension.register(uuid,
                identifier,
                owner,
                this,
                this.questPlugin.getQuestHandler(),
                this.questPlugin.getPlayerHandler(),
                configuration);

        // Parse extension values
        extension.parseSettings();

        // Return the extension
        return extension;
    }

    public Set<Task> getTasks()
    {
        return this.tasks;
    }

    public Set<Requirement> getRequirements()
    {
        return this.requirements;
    }

    public Set<Reward> getRewards()
    {
        return this.rewards;
    }

    public Task getTask(UUID uuid)
    {
        for (Task task : this.tasks)
        {
            if (task.getUUID().equals(uuid))
            {
                return task;
            }
        }

        return null;
    }
}
