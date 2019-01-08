package nl.tim.questplugin.quest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.quest.stage.Stage;
import nl.tim.questplugin.quest.stage.rewards.StageLinkReward;
import nl.tim.questplugin.quest.wrappers.RewardWrapper;
import nl.tim.questplugin.quest.wrappers.TaskWrapper;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class QuestHandler
{
    private QuestPlugin questPlugin;
    private Set<Quest> quests;

    private Set<Stage> stages;

    @Inject
    public QuestHandler(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
        this.quests = new HashSet<>();
        this.stages = new HashSet<>();
    }

    public void registerQuest(Quest quest)
    {
        this.quests.add(quest);
    }

    public void registerStage(Stage stage)
    {
        this.stages.add(stage);
    }

    public void registerQuestTrigger(Trigger trigger)
    {
        trigger.register(this, this.questPlugin.getPlayerHandler());
    }

    public Set<Quest> getQuests()
    {
        return this.quests;
    }

    public Quest getQuest(UUID uuid)
    {
        Set<UUID> uuids = Stream.of(uuid).collect(Collectors.toCollection(HashSet::new));
        Set<Quest> result = this.getQuest(uuids);

        return result.isEmpty() ? null : result.iterator().next();
    }

    public Set<Quest> getQuest(Set<UUID> uuids)
    {
        Set<Quest> result = new HashSet<>();

        for (Quest quest : this.quests)
        {
            if (uuids.contains(quest.getUUID()))
            {
                result.add(quest);
            }
        }

        return result;
    }

    public TaskWrapper getTaskWrapper(UUID uuid)
    {
        for (TaskWrapper taskWrapper : this.questPlugin.getTaskHandler().getTaskWrappers())
        {
            if (taskWrapper.getUUID().equals(uuid))
            {
                return taskWrapper;
            }
        }

        return null;
    }

    public Stage getStage(UUID uuid)
    {
        for (Stage stage : this.stages)
        {
            if (stage.getUUID().equals(uuid))
            {
                return stage;
            }
        }

        return null;
    }

    /**
     * Returns {@link Set<Quest>} containing all {@link Quest}s that the player can progress at the current location.
     * @param player {@link nl.tim.questplugin.player.QPlayer} to check
     * @return A {@link Set<Quest>} containing all {@link Quest}s that the player can progress at the current location.
     */
    public Set<Quest> getQuestAtLocation(QPlayer player)
    {
        Set<Quest> result = new HashSet<>();

        // Check all quests
        for (Quest quest : this.quests)
        {
            if (!quest.isAreaLocked() || quest.getQuestArea().inArea(player))
            {
                result.add(quest);
            }
        }

        return result;
    }

    /**
     * Returns a list containing all {@link TaskWrapper}s from the given {@link Quest} the player is actively progressing.
     * @param player {@link QPlayer} to check
     * @param quest Only {@link TaskWrapper}s of this {@link Quest} are returned
     * @return A list with the applicable {@link TaskWrapper}s.
     */
    public List<TaskWrapper> getActiveTasks(QPlayer player, Quest quest)
    {
        List<TaskWrapper> result = new ArrayList<>();
        List<UUID> tasks = player.getActiveTasks(quest);
        Stage stage = this.getActiveStage(player, quest);

        if (stage != null)
        {
            for (TaskWrapper taskWrapper : stage.getConfiguration().getTaskWrappers())
            {
                if (tasks.contains(taskWrapper.getUUID()))
                {
                    result.add(taskWrapper);

                    // Check if we're done
                    if (result.size() == tasks.size())
                    {
                        return result;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Returns the active {@link Stage} of the given {@link QPlayer} with the given {@link Quest}.
     * @param player {@link QPlayer} to check
     * @param quest Only a {@link Stage} of this {@link Quest} can be returned
     * @return A {@link Stage}, or null if none was found.
     */
    public Stage getActiveStage(QPlayer player, Quest quest)
    {
        List<UUID> tasks = player.getActiveTasks(quest);

        for (Stage stage : quest.getStages())
        {
            for (TaskWrapper taskWrapper : stage.getConfiguration().getTaskWrappers())
            {
                if (tasks.contains(taskWrapper.getUUID()))
                {
                    return stage;
                }
            }
        }

        return null;
    }

    private void handleRewards(Collection<RewardWrapper> rewards, QPlayer player)
    {
        for (RewardWrapper reward : rewards)
        {
            Player bukkitPlayer = questPlugin.getPlayerHandler().getPlayer(player);

            if (bukkitPlayer == null)
            {
                QuestPlugin.getLog().severe("Player DB went out of sync (trying to complete a quest/stage/task" +
                        " for an offline player). Player '" + player.getName() + "' with UUID '" + player.getUUID() + "'");
                //TODO: Add something to handle this
                return;
            }

            // Trigger reward
            reward.giveReward(bukkitPlayer);
        }
    }

    public boolean checkQuestComplete(QPlayer player, Quest quest)
    {
        return player.getActiveTasks(quest).isEmpty();
    }

    public boolean checkStageComplete(QPlayer player, Stage stage)
    {
        // Check if the stage has branching tasks
        if (stage.hasBranchingTasks())
        {
            boolean branchCompleted = false;

            // Check tasks for completion
            for (TaskWrapper task : stage.getConfiguration().getTaskWrappers())
            {
                boolean completed = player.hasCompletedTask(task);

                // Check if this is a branching task
                if (stage.getConfiguration().getRewardForTask(task)
                        .stream()
                        .anyMatch(rewardWrapper -> rewardWrapper.getReward() instanceof StageLinkReward))
                {
                    if (completed)
                    {
                        // Check if another branching task was completed, should not be the case
                        if (branchCompleted)
                        {
                            QuestPlugin.getLog().severe("An error occurred while checking for stage completion: " +
                                    "Stage '" + stage.getUUID() + "' has two or more completed branching tasks for player '" + player.getUUID() + "'!");
                        }

                        branchCompleted = true;
                    }
                }
            }

            return branchCompleted;

        } else
        {
            return player.getCompletedTasks(stage).size() == stage.getConfiguration().getTaskWrappers().size();
        }
    }

    public boolean checkTaskComplete(QPlayer player, TaskWrapper task)
    {
        return player.getProgress(task.getUUID()).getProgress() >= (Integer) task.getTaskConfiguration().get(task.getTask().getFinishOption());
    }

    public void processProgress(QPlayer player, Quest quest)
    {
        // First check task completion
        for (UUID taskUUID : player.getActiveTasks(quest))
        {
            TaskWrapper task = this.getTaskWrapper(taskUUID);

            if (this.checkTaskComplete(player, task))
            {
                this.completeTask(player, quest, this.getStage(task.getStageUUID()), task);
            }
        }

        // Then check stage completion
        for (UUID stageUUID : player.getActiveStages(quest))
        {
            Stage stage = this.getStage(stageUUID);

            if (this.checkStageComplete(player, stage))
            {
                this.completeStage(player, quest, stage);
            }
        }

        // Finally check quest completion
        if (this.checkQuestComplete(player, quest))
        {
            this.completeQuest(player, quest);
        }
    }

    private void completeQuest(QPlayer player, Quest quest)
    {
        // Update player progress
        player.completeQuest(quest);

        // Trigger rewards
        this.handleRewards(quest.getRewards(), player);

        // Fire event
        //TODO: Fire event
    }

    private void completeStage(QPlayer player, Quest parent, Stage stage)
    {
        // Update player progress
        player.completeStage(parent, stage);

        // Check if branch
        if (stage.isBranching())
        {
            // Get new branches
            List<UUID> newBranches = stage.getConfiguration().getStageRewards()
                    .stream()
                    .filter(rw -> rw.getReward() instanceof StageLinkReward)
                    .map(RewardWrapper::getSetting)
                    .map(UUID.class::cast)
                    .collect(Collectors.toList());

            // Was a branch to we have to do some cleaning up of other stages
            player.cleanQuestProgressAfterBranch(parent, newBranches);
        }

        // Trigger rewards
        this.handleRewards(stage.getConfiguration().getStageRewards(), player);

        // Fire event
        //TODO: Fire event
    }

    public void completeTask(QPlayer player, Quest quest, Stage parent, TaskWrapper taskWrapper)
    {
        // Update player progress
        player.completeTaskWrapper(parent, taskWrapper);

        // Check if branch
        if (parent.getConfiguration().getRewardForTask(taskWrapper).stream().anyMatch(rw -> rw.getReward() instanceof StageLinkReward))
        {
            // Was a branch so we have to do some cleaning up
            player.cleanStageProgress(quest, parent);
        }

        // Trigger rewards
        this.handleRewards(parent.getConfiguration().getRewardForTask(taskWrapper), player);

        // Fire event
        //TODO: Fire event
    }

    public boolean canStartQuest(QPlayer player, Quest quest)
    {
        // Check requirements
        for (Stage stage : quest.getFirstStages())
        {
            if (!stage.checkRequirements(player, questPlugin.getPlayerHandler().getPlayer(player)))
            {
                return false;
            }
        }

        return !player.isProgressingQuest(quest) && (!player.hasCompletedQuest(quest) || quest.isReplayable());
    }

    public boolean acceptQuest(QPlayer player, Quest quest)
    {
        // Quick check to see if the player can start the quest
        if (!this.canStartQuest(player, quest))
        {
            return false;
        }

        // Delete old progress
        player.cancelQuest(quest);
        player.clearCompletedQuest(quest);

        // Add first tasks to player progress map
        quest.getStages().getFirst().getConfiguration().getTaskWrappers().stream()
                .map(TaskWrapper::getUUID)
                .forEach(e -> player.updateProgress(e, 0));

        // Fire event
        //TODO: Fire event

        return true;
    }

}
