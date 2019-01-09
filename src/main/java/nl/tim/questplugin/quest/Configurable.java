package nl.tim.questplugin.quest;

import nl.tim.questplugin.storage.Saveable;
import nl.tim.questplugin.storage.Storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Configurable implements Saveable
{
    private Map<String, String> settingDescription;
    private Map<String, Object> configurationValues;

    public Configurable()
    {
        this.settingDescription = new HashMap<>();
        this.configurationValues = new HashMap<>();
    }

    protected void addConfiguration(String identifier, String description)
    {
        this.getSettingDescriptions().put(identifier, description);
        this.getConfigurationValues().put(identifier, null);
    }

    protected void copySettings(Map<String, Object> settings)
    {
        // Set settings
        for (String setting : settings.keySet())
        {
            this.getConfigurationValues().put(setting, settings.get(setting));
        }
    }

    public Object getSetting(String setting)
    {
        return this.configurationValues.getOrDefault(setting, null);
    }

    public Map<String, String> getSettingDescriptions()
    {
        return this.settingDescription;
    }

    public Map<String, Object> getConfigurationValues()
    {
        return this.configurationValues;
    }

    public Map<String, String> getRequiredConfiguration()
    {
        Map<String, String> result = new HashMap<>();

        for (String setting : this.getConfigurationValues().keySet())
        {
            result.put(setting, this.getSettingDescriptions().getOrDefault(setting, "No description provided"));
        }

        return result;
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add configuration
        for (String setting : this.getConfigurationValues().keySet())
        {
            data.add(new Storage.DataPair<>("configuration." + setting, this.configurationValues.get(setting).toString()));
        }

        return data;
    }
}
