package nl.tim.questplugin.quest.stage;

import nl.tim.questplugin.api.Requirement;
import nl.tim.questplugin.api.Reward;
import nl.tim.questplugin.api.Task;
import nl.tim.questplugin.quest.Owner;
import nl.tim.questplugin.quest.Quest;
import nl.tim.questplugin.quest.stage.rewards.StageLinkReward;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Stage implements Owner
{
    private String identifier;
    private Quest quest;
    private UUID uuid;
    private StageConfiguration configuration;

    // Some flags
    private boolean broken;
    private boolean branching;
    private boolean branchingTasks;

    public Stage(String identifier,
                 Quest quest,
                 UUID uuid,
                 StageConfiguration configuration,
                 boolean broken,
                 boolean branching,
                 boolean branchingTasks)
    {
        this.identifier = identifier;
        this.quest = quest;
        this.uuid = uuid;
        this.configuration = configuration;
        this.broken = broken;
        this.branching = branching;
        this.branchingTasks = branchingTasks;
    }

    /**
     * Returns a {@link StageConfiguration} instance that contains all configuration for this stage and it's tasks.
     * @return A {@link StageConfiguration} instance.
     */
    public StageConfiguration getConfiguration()
    {
        return this.configuration;
    }

    public String getIdentifier()
    {
        return this.identifier;
    }

    public boolean checkBroken()
    {
        //TODO: Add log messages

        // Check stage rewards for branching
        if (this.configuration.getStageStartRewards().stream().anyMatch(rw -> rw instanceof StageLinkReward))
        {
            // Stages cannot have branches as start reward
            this.broken = true;
            return true;
        }

        // Check rewards
        if (this.branchingTasks && this.checkBranching())
        {
            // Stages with branching tasks cannot be branching themselves
            this.broken = true;
            return true;
        }

        // All checks have succeeded
        this.broken = false;
        return false;
    }

    public boolean checkBranching()
    {
        if (this.configuration.getStageRewards().stream().anyMatch(rw -> rw instanceof StageLinkReward))
        {
            this.branching = true;
            return true;
        }

        return false;
    }

    public boolean checkBranchingTasks()
    {
        for (Task task : this.getConfiguration().getTasks())
        {
            for (Reward reward : this.getConfiguration().getRewardForTask(task))
            {
                if (reward instanceof StageLinkReward)
                {
                    this.branchingTasks = true;
                    return true;
                }
            }
        }

        this.branchingTasks = false;
        return false;
    }

    public boolean isBroken()
    {
        return this.broken;
    }

    public boolean isFloating()
    {
        return this.quest == null;
    }

    public boolean isBranching()
    {
        return this.branching;
    }

    public boolean hasBranchingTasks()
    {
        return this.branchingTasks;
    }

    public boolean checkRequirements(Player player)
    {
        for (List<Requirement> requirementGroup : this.configuration.getRequirements())
        {
            boolean requirementMet = false;

            for (Requirement requirement : requirementGroup)
            {
                if (requirement.requirementMet(player))
                {
                    requirementMet = true;
                    break;
                }
            }

            if (!requirementMet)
            {
                return false;
            }
        }

        return true;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public Quest getQuest()
    {
        return this.quest;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (object == null || getClass() != object.getClass())
        {
            return false;
        }

        Stage stage = (Stage) object;

        return new EqualsBuilder()
                .append(broken, stage.broken)
                .append(branching, stage.branching)
                .append(branchingTasks, stage.branchingTasks)
                .append(identifier, stage.identifier)
                .append(quest, stage.quest)
                .append(uuid, stage.uuid)
                .append(configuration, stage.configuration)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(identifier)
                .append(quest)
                .append(uuid)
                .append(configuration)
                .append(broken)
                .append(branching)
                .append(branchingTasks)
                .toHashCode();
    }
}
