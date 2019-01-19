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

import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.api.Requirement;
import nl.tim.questplugin.quest.Owner;
import nl.tim.questplugin.storage.Saveable;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.utils.StringUtils;
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

    public static RequirementWrapper load(QuestPlugin questPlugin, Collection<Storage.DataPair<String>> dataPairs)
    {
        List<List<Requirement>> result = new ArrayList<>();
        MultiValuedMap<Integer, Requirement> map = new ArrayListValuedHashMap<>();

        // Load all requirements into their groups
        for (Storage.DataPair dataPair : dataPairs)
        {
            String id = StringUtils.stripIncluding(dataPair.getKey(), "requirements", true);
            String groupIDRaw = id.split(".")[0];
            String rawUUID = id.substring(groupIDRaw.length() + 1);

            // Check if both group number and uuid are valid
            if (!NumberUtils.isNumber(groupIDRaw) || !StringUtils.isUUID(rawUUID))
            {
                QuestPlugin.getLog().warning("Requirement (" + rawUUID + " in " + groupIDRaw + ") failed to load: " +
                        "either group id or uuid is invalid!");
                continue;
            }

            Integer groupID = Integer.valueOf(groupIDRaw);
            UUID uuid = UUID.fromString(rawUUID);

            // Load requirement
            Requirement requirement = (Requirement) questPlugin.getExtensionImageBuilder().load(uuid);

            // Check if requirement failed to load
            if (requirement == null)
            {
                return null;
            }

            // Add requirement to group
            map.put(groupID, requirement);
        }

        // Divide into groups
        for (Integer groupID : map.keySet())
        {
            result.add(new ArrayList<>(map.get(groupID)));
        }

        return new RequirementWrapper(result);
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        for (int i = 0; i < this.requirements.size(); i++)
        {
            for (Requirement requirement : this.requirements.get(i))
            {
                data.add(new Storage.DataPair<>("requirements." + i + "." + requirement.getUUID(),
                        "REQUIREMENT"));
            }
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
