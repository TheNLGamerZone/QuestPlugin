package nl.tim.questplugin.quest;

import nl.tim.questplugin.area.Area;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Quest
{
    private UUID uuid;
    private Area questArea;
    private Set<Stage> questStages;

    // Some flags
    private boolean isBroken;
    private boolean isHidden;

    public Quest(UUID uuid, Area questArea, boolean isHidden, boolean isBroken)
    {
        this.uuid = uuid;
        this.questArea = questArea;
        this.questStages = new HashSet<>();
        this.isBroken = isBroken;
        this.isHidden = isHidden;
    }

    public Quest(UUID uuid, Area questArea, boolean isHidden)
    {
        this(uuid, questArea, false, isHidden);
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    /**
     * Reruns check on this quest to check if it is broken. Returns result.
     * @return Boolean that indicates if this quest is broken after rechecking.
     */
    public boolean checkBroken()
    {
        // Check if the quest has stages and an area
        if (this.questStages != null && this.questArea != null)
        {
            for (Stage stage : this.questStages)
            {
                // Check if there is a broken stage
                if (stage.checkBroken())
                {
                    this.isBroken = true;

                    return true;
                }
            }

            // At this points all checks have succeeded, safe to assume this quest is no longer broken
            this.isBroken = false;
        }

        return this.isBroken;
    }


    /**
     * Returns a boolean indicating whether this quest can be started/progressed.
     * @return A boolean indicating whether this quest can be started/progressed.
     */
    public boolean isAvailable()
    {
        return !this.isHidden && !this.isBroken;
    }

    /**
     * Returns a boolean indicating whether this quest is broken. If this returns true,
     * the quest is either not yet configured properly on something has failed during initialization of this quest.
     * @return A boolean indicating whether this quest is broken.
     */
    public boolean isBroken()
    {
        return this.isBroken;
    }

    /**
     * Returns a boolean indicating whether this quest is hidden (can not be viewed by players).
     * @return A boolean indicating whether this quest is hidden (can not be viewed by players).
     */
    public boolean isHidden()
    {
        return this.isHidden;
    }
}
