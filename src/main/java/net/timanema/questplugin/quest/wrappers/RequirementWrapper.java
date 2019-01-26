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

package net.timanema.questplugin.quest.wrappers;

import net.timanema.questplugin.QuestPlugin;
import net.timanema.questplugin.api.Requirement;
import net.timanema.questplugin.storage.Saveable;
import net.timanema.questplugin.storage.Storage;
import net.timanema.questplugin.quest.Owner;
import net.timanema.questplugin.utils.StringUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.entity.Player;

import java.util.*;

public class RequirementWrapper implements Saveable
{
    private List<List<Requirement>> requirements;

    public RequirementWrapper(List<List<Requirement>> requirements)
    {
        this.requirements = requirements;
    }

    public boolean checkRequirements(Player player)
    {
        for (List<Requirement> requirementGroup : this.requirements)
        {
            boolean requirementMet = false;

            for (Requirement requirement : requirementGroup)
            {
                if (requirement.requirementMet(player))
                {
                    requirementMet = true;
                    break;
                }
            }

            if (!requirementMet)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns a {@link List} of {@link List}s. All {@link Requirement}s in a {@link List}
     * form a group of requirements that have an OR relation (i.e. when at least one requirement in a group is met,
     * the whole group will be counted as 'requirement met'), while each all {@link List} have a AND relation
     * (i.e. all maps in the list have to be marked as 'requirement met' in order for the quest requirements to be met).
     * @return A {@link List} of {@link List}s.
     */
    public List<List<Requirement>> getRequirements()
    {
        return this.requirements;
    }

    public void registerOwner(Owner owner)
    {
        this.requirements.forEach(group -> group.forEach(req -> req.registerOwner(owner)));
    }

    public static RequirementWrapper load(QuestPlugin questPlugin, Collection<Storage.DataPair<Collection>> dataPairs)
    {
        List<List<Requirement>> result = new ArrayList<>();

        if (dataPairs != null)
        {
            // Load all requirements into their groups
            for (Storage.DataPair<Collection> dataPair : dataPairs)
            {
                String id = StringUtils.stripIncluding(dataPair.getKey(), "requirements", true);
                String groupIDRaw = id.split("\\.")[0];

                // Check if group number is invalid
                if (!NumberUtils.isNumber(groupIDRaw))
                {
                    QuestPlugin.getLog().warning(
                            "Requirement group " + groupIDRaw + " failed to load: " + groupIDRaw + " is not a number");
                    continue;
                }

                List<Requirement> requirements = new ArrayList<>(dataPair.getData().size());

                for (Object rawID : dataPair.getData())
                {
                    // Check if id is not a uuid
                    if (!StringUtils.isUUID(rawID.toString()))
                    {
                        QuestPlugin.getLog().warning("Requirement " + rawID + " failed to load: invalid UUID");
                        continue;
                    }

                    UUID uuid = UUID.fromString(rawID.toString());
                    Requirement requirement = (Requirement) questPlugin.getExtensionImageBuilder().load(uuid);

                    // Check if it failed to load
                    if (requirement == null)
                    {
                        return null;
                    }

                    requirements.add(requirement);
                }

                // Add requirements list
                result.add(requirements);
            }
        }

        return new RequirementWrapper(result);
    }

    @Override
    public Set<Storage.DataPair> getData()
    {
        Set<Storage.DataPair> data = new HashSet<>();

        for (int i = 0; i < this.requirements.size(); i++)
        {
            Set<String> ids = new HashSet<>();

            for (Requirement requirement : this.requirements.get(i))
            {
                ids.add(requirement.getUUID().toString());
            }

            data.add(new Storage.DataPair<>("requirements." + i, ids));
        }

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

        RequirementWrapper that = (RequirementWrapper) object;

        return new EqualsBuilder()
                .append(requirements, that.requirements)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(requirements)
                .toHashCode();
    }
}
