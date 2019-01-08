package nl.tim.questplugin.quest.wrappers;

import nl.tim.questplugin.player.QPlayer;
import nl.tim.questplugin.quest.stage.Requirement;
import nl.tim.questplugin.quest.stage.Stage;
import nl.tim.questplugin.storage.Saveable;
import nl.tim.questplugin.storage.Storage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RequirementWrapper implements Saveable
{
    private UUID uuid;
    private Requirement requirement;
    private Object setting;
    private boolean negate;

    public RequirementWrapper(UUID uuid,
                              Requirement requirement,
                              Object setting,
                              boolean negate)
    {
        this.uuid = uuid;
        this.requirement = requirement;
        this.setting = setting;
        this.negate = negate;
    }

    public boolean requirementMet(QPlayer qPlayer, Player player, Stage stage)
    {
        return negate != this.requirement.checkRequirement(qPlayer, player, stage, this.setting);
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add requirement identifier
        data.add(new Storage.DataPair<>(uuid + ".requirement", this.requirement.getIdentifier()));

        // Add setting and negation
        data.add(new Storage.DataPair<>(uuid + ".setting", this.setting.toString()));
        data.add(new Storage.DataPair<>(uuid + ".negate", this.negate + ""));

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

        RequirementWrapper that = (RequirementWrapper) object;

        return new EqualsBuilder()
                .append(negate, that.negate)
                .append(uuid, that.uuid)
                .append(requirement, that.requirement)
                .append(setting, that.setting)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(uuid)
                .append(requirement)
                .append(setting)
                .append(negate)
                .toHashCode();
    }
}
