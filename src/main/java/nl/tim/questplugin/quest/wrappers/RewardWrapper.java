package nl.tim.questplugin.quest.wrappers;

import nl.tim.questplugin.quest.stage.Reward;
import nl.tim.questplugin.storage.Saveable;
import nl.tim.questplugin.storage.Storage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RewardWrapper implements Saveable
{
    private UUID uuid;
    private Reward reward;
    private Object setting;

    public RewardWrapper(UUID uuid, Reward reward, Object setting)
    {
        this.uuid = uuid;
        this.reward = reward;
        this.setting = setting;
    }

    public void giveReward(Player player)
    {
        this.reward.giveReward(player, setting);
    }

    public Reward getReward()
    {
        return this.reward;
    }

    public Object getSetting()
    {
        return this.setting;
    }

    @Override
    public Set<Storage.DataPair<String>> getData()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();

        // Add reward and setting
        data.add(new Storage.DataPair<>(this.uuid + ".reward", this.reward.getIdentifier()));
        data.add(new Storage.DataPair<>(this.uuid + ".setting", this.setting.toString()));

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

        RewardWrapper that = (RewardWrapper) object;

        return new EqualsBuilder()
                .append(uuid, that.uuid)
                .append(reward, that.reward)
                .append(setting, that.setting)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(uuid)
                .append(reward)
                .append(setting)
                .toHashCode();
    }
}
