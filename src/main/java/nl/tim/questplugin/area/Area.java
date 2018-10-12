package nl.tim.questplugin.area;

import org.bukkit.entity.Player;

public class Area
{
    private Cube areaCube;
    private boolean heightLimited;

    public Area(Cube areaCube, boolean heightLimited)
    {
        this.areaCube = areaCube;
        this.heightLimited = heightLimited;
    }

    public boolean inArea(Player player)
    {
        return this.areaCube.inCube(player.getLocation(), this.heightLimited);
    }
}
