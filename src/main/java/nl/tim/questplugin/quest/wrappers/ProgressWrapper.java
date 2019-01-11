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

package nl.tim.questplugin.quest.wrappers;

import nl.tim.questplugin.storage.Saveable;
import nl.tim.questplugin.storage.Storage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ProgressWrapper implements Saveable
{
    private UUID uuid;
    private UUID questUUID;
    private UUID stageUUID;
    private UUID taskUUID;
    private Integer progress;

    public ProgressWrapper(UUID uuid, UUID questUUID, UUID stageUUID, UUID taskUUID, Integer progress)
    {
        this.uuid = uuid;
        this.questUUID = questUUID;
        this.stageUUID = stageUUID;
        this.taskUUID = taskUUID;
        this.progress = progress;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public UUID getQuestUUID()
    {
        return this.questUUID;
    }

    public UUID getStageUUID()
    {
        return this.stageUUID;
    }

    public UUID getTaskUUID()
    {
        return this.taskUUID;
    }

    public Integer getProgress()
    {
        return this.progress;
    }

    public void updateProgress(Integer progress)
    {
        this.progress = progress;
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Save uuids
        data.add(new Storage.DataPair<>(this.uuid + ".quest", this.questUUID.toString()));
        data.add(new Storage.DataPair<>(this.uuid + ".stage", this.stageUUID.toString()));
        data.add(new Storage.DataPair<>(this.uuid + ".task", this.taskUUID.toString()));

        // Save progress
        data.add(new Storage.DataPair<>(this.uuid + ".progress", this.progress.toString()));

        return data;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (object == null || getClass() != object.getClass())
        {
            return false;
        }

        ProgressWrapper wrapper = (ProgressWrapper) object;

        return new EqualsBuilder()
                .append(uuid, wrapper.uuid)
                .append(questUUID, wrapper.questUUID)
                .append(stageUUID, wrapper.stageUUID)
                .append(taskUUID, wrapper.taskUUID)
                .append(progress, wrapper.progress)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(uuid)
                .append(questUUID)
                .append(stageUUID)
                .append(taskUUID)
                .append(progress)
                .toHashCode();
    }
}
