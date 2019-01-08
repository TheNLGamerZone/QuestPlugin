package nl.tim.questplugin.quest.stage;

public enum StageOption
{
    TASKS_PARALLEL("Tasks can be progressed in parallel", true);

    private String description;
    private boolean toggle;

    StageOption(String description, boolean toggle)
    {
        this.description = description;
        this.toggle = toggle;
    }

    public String getDescription()
    {
        return this.description;
    }

    public boolean isToggle()
    {
        return this.toggle;
    }
}
