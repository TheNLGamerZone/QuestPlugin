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

import net.timanema.questplugin.api.Requirement;
import net.timanema.questplugin.api.Reward;
import net.timanema.questplugin.api.Task;
import net.timanema.questplugin.api.Trigger;

public enum ExtensionType
{
    REQUIREMENT(Requirement.class), REWARD(Reward.class), TASK(Task.class), TRIGGER(Trigger.class);

    private Class<? extends CustomExtension> clazz;

    ExtensionType(Class<? extends CustomExtension> clazz)
    {
        this.clazz = clazz;
    }

    public Class<? extends CustomExtension> getClazz()
    {
        return this.clazz;
    }

    public static ExtensionType get(String name)
    {
        for (ExtensionType extensionType : values())
        {
            if (extensionType.name().equalsIgnoreCase(name))
            {
                return extensionType;
            }
        }

        return null;
    }
}
