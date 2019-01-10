package nl.tim.questplugin.quest;

import nl.tim.questplugin.api.CustomExtension;
import nl.tim.questplugin.storage.Storage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Reward extends CustomExtension
{
    private String displayName;
    private String description;

    private UUID parentUUID; //TODO: Implement methods to determine parent type

    public Reward(String displayName, String description)
    {
        super();

        this.displayName = displayName;
        this.description = description;
    }

    public abstract void giveReward(Player player);

    protected void register(UUID parentUUID)
    {
        this.parentUUID = parentUUID;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getDescription()
    {
        return this.description;
    }

    protected UUID getParentUUID()
    {
        return this.parentUUID;
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

        Reward reward = (Reward) object;

        return new EqualsBuilder()
                .appendSuper(super.equals(object))
                .append(displayName, reward.displayName)
                .append(description, reward.description)
                .append(parentUUID, reward.parentUUID)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(displayName)
                .append(description)
                .append(parentUUID)
                .toHashCode();
    }
}
