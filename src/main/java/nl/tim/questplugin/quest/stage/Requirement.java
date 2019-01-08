package nl.tim.questplugin.quest.stage;

import nl.tim.questplugin.player.QPlayer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.entity.Player;

public abstract class Requirement
{
    private String identifier;
    private String displayName;
    private String description;

    public Requirement(String identifier, String displayName, String description)
    {
        this.identifier = identifier;
        this.displayName = displayName;
        this.description = description;
    }

    public abstract boolean checkRequirement(QPlayer qPlayer, Player player, Stage stage, Object setting);

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
                .append(identifier, that.identifier)
                .append(displayName, that.displayName)
                .append(description, that.description)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(identifier)
                .append(displayName)
                .append(description)
                .toHashCode();
    }
}
