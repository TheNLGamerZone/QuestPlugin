package nl.tim.questplugin.api;

import nl.tim.questplugin.quest.CustomExtension;
import nl.tim.questplugin.quest.Owner;
import nl.tim.questplugin.storage.Storage;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Reward extends CustomExtension
{
    public Reward(String displayName, String description)
    {
        super(displayName, description);
    }

    public abstract void giveReward(Player player);

    protected UUID getParentUUID()
    {
        return this.getOwner().getUUID();
    }

    protected Owner getParent()
    {
        return this.getOwner();
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add reward
        data.add(new Storage.DataPair<>(this.getUUID() + ".reward", this.getIdentifier()));

        // Add configuration
        Set<Storage.DataPair<String>> configuration = super.getData();

        configuration.forEach(dp -> dp.prependKey(this.getUUID() + "."));
        data.addAll(configuration);

        return data;
    }
}
