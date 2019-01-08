package nl.tim.questplugin.area;

import nl.tim.questplugin.player.QPlayer;

import java.util.Objects;
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

    public boolean inArea(QPlayer player)
    {
        // Loop through all regions linked to this area
        for (Region region : regions)
        {
            // Check if the player is in this regio
            if (region.inRegion(player.getLastLocation()))
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

    @Override
    public int hashCode()
    {
        return Objects.hash(this.uuid, this.regions);
    }
}
