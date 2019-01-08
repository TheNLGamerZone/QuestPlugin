package nl.tim.questplugin.quest.tasks;

public enum TaskOption
{
    NAME("Placeholder", Object.class),
    WILDCARD("The developer of your custom task could not find a suitable option to describe this option. " +
            "Please check the wiki/documentation of the plugin that added this custom task.", Integer.class);

    private String description;
    private Class expectedInput;

    TaskOption(String description, Class expectedInput)
    {
        this.description = description;
        this.expectedInput = expectedInput;
    }

    public String getDescription()
    {
        return this.description;
    }

    public Class getExpectedInput()
    {
        return this.expectedInput;
    }
}
