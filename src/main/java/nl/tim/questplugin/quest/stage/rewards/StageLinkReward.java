package nl.tim.questplugin.quest.stage.rewards;

import nl.tim.questplugin.quest.Reward;
import org.bukkit.entity.Player;

/**
 * Advanced reward used for linking floating stages to a quest, to implement branching quests.
 * Setting: {@link java.util.UUID}
 * Behaviour: Link the set {@link nl.tim.questplugin.quest.stage.Stage} to the quest
 */
@RewardInformation(identifier = "stage_link", author = "Tim")
public class StageLinkReward extends Reward
{
    public StageLinkReward()
    {
        super("Link another stage (branching)",
                "Upon receiving this reward the set stage will be linked to the quest. " +
                        "When a link is established between a quest and stage, the other branches of the original quest will be cancelled for the player." +
                        "The linked stages will be displayed as a regular stage in the player progress view. " +
                        "Only floating stages can be linked.");
    }


    @Override
    public void giveReward(Player player)
    {
        //TODO: Implement
    }
}
