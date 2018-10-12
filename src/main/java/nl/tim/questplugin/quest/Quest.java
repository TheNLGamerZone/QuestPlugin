package nl.tim.questplugin.quest;

import nl.tim.questplugin.area.Area;

public class Quest
{
    private String displayName;
    private String questText;
    private int questID;

    private Area questArea;

    public Quest(String displayName, String questText, int questID, Area questArea)
    {
        this.displayName = displayName;
        this.questText = questText;
        this.questID = questID;
        this.questArea = questArea;
    }
}
