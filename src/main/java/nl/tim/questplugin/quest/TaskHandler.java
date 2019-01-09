package nl.tim.questplugin.quest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.quest.stage.Requirement;
import nl.tim.questplugin.quest.stage.Stage;
import nl.tim.questplugin.quest.stage.requirements.RequirementInformation;
import nl.tim.questplugin.quest.stage.rewards.RewardInformation;
import nl.tim.questplugin.quest.tasks.TaskInformation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Singleton
public class TaskHandler
{
    private Map<String, Class<? extends Task>> baseTasks;
    private Map<String, Class<? extends Reward>> baseRewards;
    private Map<String, Class<? extends Requirement>> baseRequirements;

    private Set<Task> tasks;
    private Set<Requirement> requirements;
    private Set<Reward> rewards;

    private QuestPlugin questPlugin;

    @Inject
    public TaskHandler(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
        this.baseTasks = new HashMap<>();
        this.baseRewards = new HashMap<>();
        this.baseRequirements = new HashMap<>();

        this.tasks = new HashSet<>();
        this.requirements = new HashSet<>();
        this.rewards = new HashSet<>();
    }

    public boolean registerTask(Class<? extends Task> task)
    {
        // Check if annotation is not present
        if (!task.isAnnotationPresent(TaskInformation.class))
        {
            QuestPlugin.getLog().warning("Trying to register task '" + task.getSimpleName() + "', " +
                    "but it does not contain the needed information!");
            return false;
        }

        // Get information
        TaskInformation taskInformation = task.getAnnotation(TaskInformation.class);

        QuestPlugin.getLog().info("Registered task '" + task.getSimpleName() + "' by " + taskInformation.author());

        // Add it to the base tasks
        this.baseTasks.put(taskInformation.identifier(), task);

        return true;
    }

    public boolean registerRequirement(Class<? extends Requirement> requirement)
    {
        // Check if annotation is not present
        if (!requirement.isAnnotationPresent(RequirementInformation.class))
        {
            QuestPlugin.getLog().warning("Trying to register requirement '" + requirement.getSimpleName() + "', " +
                    "but it does not contain the needed information!");
            return false;
        }

        // Get information
        RequirementInformation requirementInformation = requirement.getAnnotation(RequirementInformation.class);

        QuestPlugin.getLog().info("Registered requirement '" + requirement.getSimpleName() + "' by " + requirementInformation.author());

        // Add it to the base tasks
        this.baseRequirements.put(requirementInformation.identifier(), requirement);

        return true;
    }

    public boolean registerReward(Class<? extends Reward> reward)
    {
        // Check if annotation is not present
        if (!reward.isAnnotationPresent(RewardInformation.class))
        {
            QuestPlugin.getLog().warning("Trying to register reward '" + reward.getSimpleName() + "', " +
                    "but it does not contain the needed information!");
            return false;
        }

        // Get information
        RewardInformation rewardInformation = reward.getAnnotation(RewardInformation.class);

        QuestPlugin.getLog().info("Registered reward '" + reward.getSimpleName() + "' by " + rewardInformation.author());

        // Add it to the base tasks
        this.baseRewards.put(rewardInformation.identifier(), reward);

        return true;
    }

    public Task buildTask(String identifier,
                          Stage stage,
                          UUID uuid,
                          Map<String, Object> settings)
    {
        Class<? extends Task> taskClazz = this.baseTasks.get(identifier);

        // Check if task was loaded
        if (taskClazz == null)
        {
            // This is probably a external task, so we just ignore this one and hopefully the external plugin will
            // have registered its task after this one started up. If not the admin(s) will get a message on join
            return null;
        }

        // Create new instance of task
        Constructor<? extends Task> constructor;
        Task task;

        try
        {
             constructor = taskClazz.getConstructor();
             task = constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            QuestPlugin.getLog().severe("An error occurred while trying to build a new task of type '" + identifier + "': ");
            e.printStackTrace();

            return null;
        }

        // Register task
        task.register(stage,
                uuid,
                identifier,
                this,
                this.questPlugin.getQuestHandler(),
                this.questPlugin.getPlayerHandler(),
                settings);

        // Register task as listener with bukkit
        this.questPlugin.getServer().getPluginManager().registerEvents(task, this.questPlugin);

        // Finally return task
        return task;
    }

    public Reward buildReward(String identifier,
                              UUID uuid,
                              UUID parentUUID,
                              Map<String, Object> settings)
    {
        Class<? extends Reward> rewardClazz = this.baseRewards.get(identifier);

        // Check if reward was loaded
        if (rewardClazz == null)
        {
            // This is probably a external reward, so we just ignore this one and hopefully the external plugin will
            // have registered its reward after this one started up. If not the admin(s) will get a message on join
            return null;
        }

        // Create new instance of task
        Constructor<? extends Reward> constructor;
        Reward reward;

        try
        {
            constructor = rewardClazz.getConstructor();
            reward = constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            QuestPlugin.getLog().severe("An error occurred while trying to build a new reward of type '" + identifier + "': ");
            e.printStackTrace();

            return null;
        }

        // Register reward
        reward.register(uuid,
                parentUUID,
                identifier,
                this,
                this.questPlugin.getQuestHandler(),
                this.questPlugin.getPlayerHandler(),
                settings);

        // Finally return reward
        return reward;
    }

    public Set<Task> getTasks()
    {
        return this.tasks;
    }

    public Set<Requirement> getRequirements()
    {
        return this.requirements;
    }

    public Set<Reward> getRewards()
    {
        return this.rewards;
    }

    public Task getTask(UUID uuid)
    {
        for (Task task : this.tasks)
        {
            if (task.getTaskUUID().equals(uuid))
            {
                return task;
            }
        }

        return null;
    }
}
