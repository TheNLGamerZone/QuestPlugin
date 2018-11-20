package nl.tim.questplugin.utils;

import nl.tim.questplugin.storage.Storage;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class SetUtils
{
    public <K extends String, V> Set<Storage.DataPair<K, V>> searchSetForKeys(Set<Storage.DataPair<K, V>> searchSet,
                                                                              String section)
    {
        Set<Storage.DataPair<K, V>> searchedSet = new HashSet<>();

        for (Storage.DataPair<K, V> dataPair : searchSet)
        {
            K fullKey = dataPair.getKey();

            // Check if the current key is in the given section
            if (!fullKey.startsWith(section))
            {
                continue;
            }


            K newKey = (K) fullKey.replace(section + ".", "");
            V data = dataPair.getData();
            Storage.DataPair<K, V> searchedPair = new Storage.DataPair<>(newKey, data);
        }
        return null;
    }
}
