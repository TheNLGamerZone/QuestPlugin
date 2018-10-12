package nl.tim.questplugin.storage.image;

import java.util.UUID;

public interface ImageBuilder<T>
{
    void save(T t);

    T load(UUID uuid);
}
