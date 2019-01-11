package nl.tim.questplugin.api;

import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.storage.Saveable;
import nl.tim.questplugin.storage.Storage;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

public abstract class Configurable implements Saveable
{
    private Map<String, String> settingDescription;
    private Map<String, Object> configurationValues;
    private Map<String, InputType> requiredType;

    public Configurable()
    {
        this.settingDescription = new HashMap<>();
        this.configurationValues = new HashMap<>();
        this.requiredType = new HashMap<>();
    }

    protected void addConfiguration(String identifier, String description, InputType type)
    {
        this.settingDescription.put(identifier, description);
        this.configurationValues.put(identifier, null);
        this.requiredType.put(identifier, type);
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

    public boolean insertSetting(String identifier, String setting)
    {
        return this.insertSetting(identifier, setting, true);
    }

    private boolean insertSetting(String identifier, String setting, boolean insert)
    {
        InputType inputType = this.requiredType.get(identifier);

        // Check if setting was valid
        if (!this.isValidSettingType(identifier, setting))
        {
            QuestPlugin.getLog().warning("Trying to parse invalid setting '" + setting + "' to type " + inputType.name());
            return false;
        }

        // Verify with apache commons
        switch (inputType)
        {
            case NUMBER:
                if (NumberUtils.isNumber(setting))
                {
                    if (insert)
                    {
                        this.configurationValues.put(identifier, NumberUtils.createDouble(setting));
                    }
                    return true;
                }

                return false;
            case BOOLEAN:
                if (BooleanUtils.toBooleanObject(setting) != null)
                {
                    if (insert)
                    {
                        this.configurationValues.put(identifier, BooleanUtils.toBoolean(setting));
                    }
                    return true;
                }

                return false;
            case STRING:
                if (insert)
                {
                    this.configurationValues.put(identifier, setting);
                }
                return true;
        }

        // Shouldn't happen but I'll return false just in case
        return false;
    }

    public boolean isValidSettingType(String identifier, String setting)
    {
        return this.insertSetting(identifier, setting, false);
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
