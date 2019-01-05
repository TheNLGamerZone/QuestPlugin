package nl.tim.questplugin.area;

import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class Area
{
    private UUID uuid;
    private Set<Region> regions;

    public Area(UUID uuid, Set<Region> regions)
    {
        this.uuid = uuid;
        this.regions = regions;
    }

    public boolean inArea(Player player)
    {
        // Loop through all regions linked to this area
        for (Region region : regions)
        {
            // Check if the player is in this regio
            if (region.inRegion(player.getLocation()))
            {
                return true;
            }
        }

        return false;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public Set<Region> getRegions()
    {
        return this.regions;
    }
}
