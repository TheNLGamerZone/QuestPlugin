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
