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

package nl.tim.questplugin.utils;

import nl.tim.questplugin.storage.Storage;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class SetUtils
{
    /**
     * Checks a given data set for the given section and returns a set containing all {@link nl.tim.questplugin.storage.Storage.DataPair}
     * which are in the given section.
     * @param searchSet set to check
     * @param section section to search for
     * @param <V> type of {@link nl.tim.questplugin.storage.Storage.DataPair}
     * @return A set containing all {@link nl.tim.questplugin.storage.Storage.DataPair} which are in the given section.
     */
    public static <V> Set<Storage.DataPair<V>> searchSetForKeys(Set<Storage.DataPair<V>> searchSet,
                                                                              String section)
    {
        Set<Storage.DataPair<V>> searchedSet = new HashSet<>();

        for (Storage.DataPair<V> dataPair : searchSet)
        {
            String fullKey = dataPair.getKey();

            // Check if the current key is in the given section
            if (!fullKey.startsWith(section))
            {
                continue;
            }


            String newKey = fullKey.replace(section + ".", "");
            V data = dataPair.getData();
            Storage.DataPair<V> searchedPair = new Storage.DataPair<>(newKey, data);

            searchedSet.add(searchedPair);
        }

        return searchedSet;
    }
}
