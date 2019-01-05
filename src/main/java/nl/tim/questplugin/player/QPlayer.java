package nl.tim.questplugin.player;

import java.util.Map;
import java.util.UUID;

public class QPlayer
{
    private Map<UUID, Integer> progress;
    private UUID uuid;
    private String name;

    public QPlayer(Map<UUID, Integer> progress, UUID uuid, String name)
    {
        this.progress = progress;
        this.uuid = uuid;
        this.name = name;
    }
}
