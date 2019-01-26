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

package net.timanema.questplugin.quest;

import net.timanema.questplugin.api.InputType;
import net.timanema.questplugin.storage.Saveable;
import net.timanema.questplugin.storage.Storage;
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

    protected void parseSettings()
    {
        for (String setting : this.configurationValues.keySet())
        {
            this.insertSetting(setting, this.configurationValues.get(setting).toString(), true);
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


    public boolean isValidSettingType(String identifier, String setting)
    {
        return this.insertSetting(identifier, setting, false);
    }

    private boolean insertSetting(String identifier, String setting, boolean insert)
    {
        InputType inputType = this.requiredType.get(identifier);

        if (inputType == null)
        {
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
            default:
                return false;
        }
    }

    @Override
    public Set<Storage.DataPair> getData()
    {
        Set<Storage.DataPair> data = new HashSet<>();

        // Add configuration
        for (String setting : this.getConfigurationValues().keySet())
        {
            data.add(new Storage.DataPair<>("configuration." + setting, this.configurationValues.get(setting).toString()));
        }

        return data;
    }
}
