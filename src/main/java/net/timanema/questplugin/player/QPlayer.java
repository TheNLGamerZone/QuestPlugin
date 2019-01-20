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

package net.timanema.questplugin.player;

import net.timanema.questplugin.api.Task;
import net.timanema.questplugin.quest.Quest;
import net.timanema.questplugin.quest.wrappers.ProgressWrapper;
import net.timanema.questplugin.quest.stage.Stage;
import org.apache.commons.collections4.MultiValuedMap;
import org.bukkit.Location;

import java.util.*;
import java.util.stream.Collectors;

public class QPlayer
{
    private List<ProgressWrapper> progress;

    private Set<UUID> completedQuests;
    private MultiValuedMap<UUID, UUID> completedStages;
    private MultiValuedMap<UUID, UUID> completedTasks;

    private UUID uuid;
    private String name;
    private Location location;

    public QPlayer(List<ProgressWrapper> progress,
                   Set<UUID> completedQuests,
                   MultiValuedMap<UUID, UUID> completedStages,
                   MultiValuedMap<UUID, UUID> completedTasks,
                   UUID uuid,
                   String name,
                   Location location)
    {
        this.progress = progress;
        this.completedQuests = completedQuests;
        this.completedStages = completedStages;
        this.completedTasks = completedTasks;
        this.uuid = uuid;
        this.name = name;
        this.location = location;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public String getName()
    {
        return this.name;
    }

    public List<ProgressWrapper> getProgressWrappers()
    {
        return this.progress;
    }

    /**
     * Removes progress for the given {@link Quest}, effectively cancelling the quest.
     * @param quest {@link Quest} to cancel
     */
    public void cancelQuest(Quest quest)
    {
        // Check if quest is null
        if (quest == null)
        {
            return;
        }

        // Remove progress
        for (Stage stage : quest.getStages())
        {
            for (Task task : stage.getConfiguration().getTasks())
            {
                this.progress.remove(this.getProgress(task.getUUID()));
            }
        }

        // Remove completed tasks & stages
        this.clearCompletedQuest(quest);
    }

    /**
     * Removes all data from the given {@link Quest} for this player.
     * @param quest {@link Quest} to clear
     */
    public void clearCompletedQuest(Quest quest)
    {
        // Check if quest is null
        if (quest == null)
        {
            return;
        }

        // Remove tasks
        for (Stage stage : quest.getStages())
        {
            this.completedTasks.remove(stage.getUUID());
        }

        // Remove stage
        this.completedStages.remove(quest.getUUID());

        // Remove quest
        this.completedQuests.remove(quest.getUUID());
    }

    /**
     * Returns the {@link ProgressWrapper} of this player for the given task UUID.
     * @param taskUUID {@link UUID} to check
     * @return {@link ProgressWrapper} or null if not progressing.
     */
    public ProgressWrapper getProgress(UUID taskUUID)
    {
        for (ProgressWrapper wrapper : this.progress)
        {
            if (wrapper.getTaskUUID().equals(taskUUID))
            {
                return wrapper;
            }
        }

        return null;
    }

    /**
     * Updates the progress of {@link Task} indicated by the given {@link UUID} for this player.
     * @param taskUUID {@link UUID} of the {@link Task} to update
     * @param progress New progress value
     */
    public void updateProgress(UUID taskUUID, int progress)
    {
        if (uuid != null)
        {
            ProgressWrapper wrapper = this.getProgress(taskUUID);

            if (wrapper != null)
            {
                wrapper.updateProgress(progress);
            }
        }
    }

    /**
     * Removes all progress from a given {@link Stage} without adding them to the completed data for this player.
     * @param parent Parent {@link Quest} of {@link Stage}
     * @param stage {@link Stage} to remove progress from
     */
    public void cleanStageProgress(Quest parent, Stage stage)
    {
        if (parent != null && stage != null)
        {
            for (Task task : stage.getConfiguration().getTasks())
            {
                this.progress.remove(this.getProgress(task.getUUID()));
            }
        }
    }

    /**
     * Cleans old branches from the given {@link Quest} after the player started a floating stage. The ID's of the floating
     * stages should be specified in the list, those stages will not be cleared from progress.
     * @param quest {@link Quest} to clean up
     * @param excludeFromClean List of {@link UUID}s of the floating stages that should not be cleaned
     */
    public void cleanQuestProgressAfterBranch(Quest quest, List<UUID> excludeFromClean)
    {
        if (quest != null)
        {
            this.progress.removeIf(pw -> pw.getQuestUUID().equals(quest.getUUID()) && !excludeFromClean.contains(pw.getStageUUID()));
        }
    }

    public void completeTaskWrapper(Stage parent, Task task)
    {
        if (parent != null && task != null)
        {
            this.progress.remove(this.getProgress(task.getUUID()));
            this.completedTasks.put(parent.getUUID(), task.getUUID());
        }
    }

    public void completeStage(Quest parent, Stage stage)
    {
        // Check if parent or child are null
        if (parent == null || stage == null)
        {
            return;
        }

        // Remove tasks from progress
        this.cleanStageProgress(parent, stage);

        // Add stage to completed list
        this.completedStages.put(parent.getUUID(), stage.getUUID());
    }

    public void completeQuest(Quest quest)
    {
        if (quest != null)
        {
            this.completedQuests.add(quest.getUUID());
        }
    }

    public boolean hasCompletedQuest(Quest quest)
    {
        return this.completedQuests.contains(quest.getUUID());
    }

    public boolean hasCompletedStage(Stage stage)
    {
        return this.completedStages.containsValue(stage.getUUID());
    }

    public boolean hasCompletedTask(Task task)
    {
        return this.completedTasks.containsValue(task.getUUID());
    }

    public boolean isProgressingQuest(Quest quest)
    {
        return quest != null && !this.getActiveTasks(quest, true).isEmpty();
    }

    public List<UUID> getActiveTasks(Quest quest)
    {
        return quest != null ? this.getActiveTasks(quest, false) : null;
    }

    private List<UUID> getActiveTasks(Quest quest, boolean search)
    {
        // Check if quest was null
        if (quest == null)
        {
            return null;
        }

        List<UUID> result = new ArrayList<>();

        for (ProgressWrapper wrapper : this.progress)
        {
            if (wrapper.getQuestUUID().equals(quest.getUUID()))
            {
                result.add(wrapper.getTaskUUID());

                // Check if this was just a search
                if (search)
                {
                    return result;
                }
            }
        }

        return result;
    }

    public Set<UUID> getActiveStages(Quest quest)
    {
        Set<UUID> result = new HashSet<>();

        for (ProgressWrapper progress : this.progress)
        {
            if (progress.getQuestUUID().equals(quest.getUUID()))
            {
                result.add(progress.getStageUUID());
            }
        }

        return result;
    }

    public Set<UUID> getActiveTasks()
    {
        return this.progress.stream().map(ProgressWrapper::getTaskUUID).collect(Collectors.toSet());
    }

    public List<UUID> getCompletedTasks(Stage stage)
    {
        return stage != null ? new ArrayList<>(this.completedTasks.get(stage.getUUID())) : null;
    }

    /**
     * Returns the last {@link Location} of this player.
     * @return The last {@link Location} of this player.
     */
    public Location getLastLocation()
    {
        return this.location;
    }

    /**
     * Updates this player's {@link Location}.
     * @param location New {@link Location}
     */
    public void updateLocation(Location location)
    {
        this.location = location;
    }
}
