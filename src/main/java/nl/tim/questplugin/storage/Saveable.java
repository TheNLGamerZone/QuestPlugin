package nl.tim.questplugin.storage;

import java.util.Set;

public interface Saveable
{
    Set<Storage.DataPair<String>> getData();
}
