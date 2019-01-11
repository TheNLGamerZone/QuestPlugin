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

import nl.tim.questplugin.api.Requirement;
import nl.tim.questplugin.api.Reward;
import nl.tim.questplugin.api.Trigger;
import nl.tim.questplugin.area.Area;
import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.quest.stage.Stage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.entity.Player;

import java.util.*;

public class Quest implements Owner
{
    private UUID uuid;
    private Area questArea;
    private LinkedList<Stage> questStages;
    private Set<Reward> rewards;
    private Set<Trigger> triggers;
    private List<List<Requirement>> requirements;

    private boolean areaLocked;
    private boolean replayable;

    // Some flags
    private boolean broken;
    private boolean hidden;
    private boolean branching;
    private boolean sequential;

    protected Quest(UUID uuid, 
                    Area questArea,
                    LinkedList<Stage> questStages,
                    Set<Reward> rewards,
                    Set<Trigger> triggers,
                    List<List<Requirement>> requirements,
                    boolean areaLocked,
                    boolean replayable,
                    boolean hidden, 
                    boolean branching,
                    boolean sequential,
                    boolean broken)
    {
        this.uuid = uuid;
        this.questArea = questArea;
        this.questStages = questStages;
        this.rewards = rewards;
        this.triggers = triggers;
        this.requirements = requirements;
        this.areaLocked = areaLocked;
        this.replayable = replayable;
        this.hidden = hidden;
        this.branching = branching;
        this.sequential = sequential;
        this.broken = broken;
    }

    public Quest(UUID uuid,
                 Area questArea,
                 LinkedList<Stage> questStages,
                 Set<Reward> rewards,
                 Set<Trigger> triggers,
                 List<List<Requirement>> requirements,
                 boolean areaLocked,
                 boolean replayable,
                 boolean hidden,
                 boolean branching,
                 boolean sequential)
    {
        this(uuid, questArea, questStages, rewards, triggers, requirements, areaLocked, replayable, hidden, branching, sequential, false);
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    /**
     * Returns this quest's {@link Area}.
     * @return This quest's {@link Area}.
     */
    public Area getQuestArea()
    {
        return this.questArea;
    }

    public Set<Trigger> getTriggers()
    {
        return this.triggers;
    }

    /**
     * Reruns check on this quest to check if it is broken. Returns result.
     * @return Boolean that indicates if this quest is broken after rechecking.
     */
    public boolean checkBroken()
    {
        // Check if the quest has stages and an area
        if (this.questStages != null && (!this.areaLocked && this.questArea != null))
        {
            for (Stage stage : this.questStages)
            {
                // Check if there is a broken stage
                if (stage.checkBroken())
                {
                    this.broken = true;
                    return true;
                }
            }

            // At this points all checks have succeeded, safe to assume this quest is no longer broken
            this.broken = false;
        }

        return this.broken;
    }

    public boolean checkBranches()
    {
        for (Stage stage : this.getStages())
        {
            if (stage.hasBranchingTasks() || stage.isBranching())
            {
                this.branching = true;
                return true;
            }
        }

        this.branching = false;
        return false;
    }


    /**
     * Returns a boolean indicating whether this quest can be started/progressed.
     * @return True if this quest can be started/progressed.
     */
    public boolean isAvailable()
    {
        return !this.hidden && !this.broken;
    }

    /**
     * Returns a boolean indicating whether this quest is broken. If this returns true,
     * the quest is either not yet configured properly on something has failed during initialization of this quest.
     * @return True if this quest is broken.
     */
    public boolean isBroken()
    {
        return this.broken;
    }

    /**
     * Returns a boolean indicating whether this quest is hidden (can not be viewed by players).
     * @return True if  this quest is hidden (can not be viewed by players).
     */
    public boolean isHidden()
    {
        return this.hidden;
    }

    /**
     * Returns a boolean indicating whether this quest requires an {@link Area} or can be progressed anywhere.
     * @return True if this quest requires an {@link Area} or can be progressed anywhere.
     */
    public boolean isAreaLocked()
    {
        return this.areaLocked;
    }

    /**
     * Returns a boolean indicating whether this quest is replayable.
     * @return True if this quest is replayable.
     */
    public boolean isReplayable()
    {
        return this.replayable;
    }

    /**
     * Returns a boolean indicating whether this quest has branches or not.
     * @return True if this quest has branches, false otherwise.
     */
    public boolean hasBranches()
    {
        return this.branching;
    }

    /**
     * Returns a boolean indicating whether this quest's stages have to be completed sequentially or can be done in parallel.
     * Do note that a quest is automatically parallel if it has branching stages.
     * @return True if the stages have to be completed sequentially, false otherwise.
     */
    public boolean isSequential()
    {
        return this.sequential || this.hasBranches();
    }

    /**
     * Returns a {@link List} of {@link List}s. All {@link Requirement}s in a {@link List}
     * form a group of requirements that have an OR relation (i.e. when at least one requirement in a group is met,
     * the whole group will be counted as 'requirement met'), while each all {@link List} have a AND relation
     * (i.e. all maps in the list have to be marked as 'requirement met' in order for the quest requirements to be met).
     * @return A {@link List} of {@link List}s.
     */
    public List<List<Requirement>> getRequirements()
    {
        return this.requirements;
    }

    public boolean checkRequirements(Player player)
    {
        for (List<Requirement> requirementGroup : this.requirements)
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

    public List<Stage> getFirstStages()
    {
        List<Stage> firstStages = new ArrayList<>();

        if (isSequential())
        {
            firstStages.add(this.questStages.getFirst());
        } else
        {
            firstStages.addAll(this.questStages);
        }

        return firstStages;
    }

    public LinkedList<Stage> getStages()
    {
        return this.questStages;
    }

    public Set<Reward> getRewards()
    {
        return this.rewards;
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

        Quest quest = (Quest) object;

        return new EqualsBuilder()
                .append(areaLocked, quest.areaLocked)
                .append(replayable, quest.replayable)
                .append(broken, quest.broken)
                .append(hidden, quest.hidden)
                .append(branching, quest.branching)
                .append(uuid, quest.uuid)
                .append(questArea, quest.questArea)
                .append(questStages, quest.questStages)
                .append(rewards, quest.rewards)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(uuid)
                .append(questArea)
                .append(questStages)
                .append(rewards)
                .append(areaLocked)
                .append(replayable)
                .append(broken)
                .append(hidden)
                .append(branching)
                .toHashCode();
    }
}
