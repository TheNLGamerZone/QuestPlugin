package nl.tim.questplugin.quest.stage;

import nl.tim.questplugin.api.CustomExtension;
import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.storage.Storage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class Requirement extends CustomExtension
{
    private String displayName;
    private String description;
    private boolean negate;

    public Requirement(String displayName, String description)
    {
        this.displayName = displayName;
        this.description = description;
    }

    public void register(boolean negate)
    {
        this.negate = negate;
    }

    public abstract boolean checkRequirement(QPlayer qPlayer, Player player);

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getDescription()
    {
        return this.description;
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add reward
        data.add(new Storage.DataPair<>(this.getUUID() + ".requirement", this.getIdentifier()));
        data.add(new Storage.DataPair<>(this.getUUID() + ".negate", this.negate + ""));

        // Add configuration
        Set<Storage.DataPair<String>> configuration = super.getData();

        configuration.forEach(dp -> dp.prependKey(this.getUUID() + "."));
        data.addAll(configuration);

        return data;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (object == null || getClass() != object.getClass())
        {
            return false;
        }

        Requirement that = (Requirement) object;

        return new EqualsBuilder()
                .appendSuper(super.equals(object))
                .append(negate, that.negate)
                .append(displayName, that.displayName)
                .append(description, that.description)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(displayName)
                .append(description)
                .append(negate)
                .toHashCode();
    }
}
