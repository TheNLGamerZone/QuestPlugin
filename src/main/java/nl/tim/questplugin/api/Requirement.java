package nl.tim.questplugin.api;

import nl.tim.questplugin.quest.CustomExtension;
import nl.tim.questplugin.quest.stage.Stage;
import nl.tim.questplugin.storage.Storage;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class Requirement extends CustomExtension
{
    public Requirement(String displayName, String description)
    {
        super(displayName, description);
    }

    protected Stage getStage()
    {
        return (Stage) this.getOwner();
    }

    protected abstract boolean checkRequirement(Player player);

    public boolean requirementMet(Player player)
    {
        boolean negate = Boolean.valueOf(this.getSetting("negate").toString());

        return negate != this.checkRequirement(player);
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add reward
        data.add(new Storage.DataPair<>(this.getUUID() + ".requirement", this.getIdentifier()));

        // Add configuration
        Set<Storage.DataPair<String>> configuration = super.getData();

        configuration.forEach(dp -> dp.prependKey(this.getUUID() + "."));
        data.addAll(configuration);

        return data;
    }
}
