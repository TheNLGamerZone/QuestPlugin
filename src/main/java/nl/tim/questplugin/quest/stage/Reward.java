package nl.tim.questplugin.quest.stage;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.bukkit.entity.Player;

import java.util.Objects;

public abstract class Reward
{
    private String identifier;
    private String displayName;
    private String description;

    public Reward(String identifier, String displayName, String description)
    {
        this.identifier = identifier;
        this.displayName = displayName;
        this.description = description;
    }

    public abstract void giveReward(Player player, Object setting);

    public String getIdentifier()
    {
        return this.identifier;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getDescription()
    {
        return this.description;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.identifier, this.displayName, this.description);
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (this.getClass() != object.getClass())
        {
            return false;
        }

        Reward reward = (Reward) object;

        return new EqualsBuilder()
                .append(this.identifier, reward.getIdentifier())
                .append(this.displayName, reward.getDisplayName())
                .append(this.description, reward.getDescription())
                .isEquals();
    }
}
