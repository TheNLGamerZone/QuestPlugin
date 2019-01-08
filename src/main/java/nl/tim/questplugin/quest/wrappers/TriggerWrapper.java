package nl.tim.questplugin.quest.wrappers;

import nl.tim.questplugin.quest.Trigger;
import nl.tim.questplugin.storage.Saveable;
import nl.tim.questplugin.storage.Storage;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TriggerWrapper implements Saveable
{
    private UUID uuid;
    private Trigger trigger;
    private Object setting;

    public TriggerWrapper(UUID uuid, Trigger trigger, Object setting)
    {
        this.uuid = uuid;
        this.trigger = trigger;
        this.setting = setting;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public Trigger getTrigger()
    {
        return this.trigger;
    }

    public Object getSetting()
    {
        return this.setting;
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add trigger and setting
        data.add(new Storage.DataPair<>(this.uuid + ".trigger", this.trigger.getIdentifier()));
        data.add(new Storage.DataPair<>(this.uuid + ".setting", this.setting.toString()));

        return data;
    }
}
