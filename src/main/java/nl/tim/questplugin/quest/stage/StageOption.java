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
