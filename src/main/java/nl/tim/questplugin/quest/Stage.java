package nl.tim.questplugin.quest;

import java.util.UUID;

public class Stage
{
    private UUID uuid;

    // Some flags
    private boolean isBroken;

    public Stage(UUID uuid, boolean isBroken)
    {
        this.uuid = uuid;
        this.isBroken = isBroken;
    }

    protected boolean checkBroken()
    {
        //TODO: Implement checks
        return this.isBroken;
    }

    protected boolean isBroken()
    {
        return this.isBroken;
    }
}
