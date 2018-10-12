package nl.tim.questplugin.storage;

import java.util.UUID;

public interface Storage
{
    void save(UUID uuid);

    void load(UUID uuid);
}
