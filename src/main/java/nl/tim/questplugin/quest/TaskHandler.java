package nl.tim.questplugin.quest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.api.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Singleton
public class TaskHandler
{
    private Map<String, Class<? extends CustomExtension>> baseTasks;
    private Map<String, Class<? extends CustomExtension>> baseRewards;
    private Map<String, Class<? extends CustomExtension>> baseRequirements;

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

    public boolean registerCustomExtension(Class<? extends CustomExtension> extension)
    {
        Class<?> superClazz = extension.getSuperclass();

        // Check if extension has annotation or not parent (idk how the latter would happen but ok)
        if (superClazz == null || !extension.isAnnotationPresent(ExtensionInformation.class))
        {
            QuestPlugin.getLog().warning("Trying to register extension '" + extension.getSimpleName() + "', " +
                    "but it does not contain the needed information!");
            return false;
        }

        // Get needed information
        ExtensionInformation extensionInformation = extension.getAnnotation(ExtensionInformation.class);
        String identifier = extensionInformation.identifier();

        QuestPlugin.getLog().info("Registered extension '" + extension.getSimpleName() + "' by " + extensionInformation.author());


        // Check super class
        if (Task.class.isAssignableFrom(extension) && extension != Task.class)
        {
            this.baseTasks.put(identifier, extension);
        } else if (Requirement.class.isAssignableFrom(extension) && extension != Requirement.class)
        {
            this.baseRequirements.put(identifier, extension);
        } else if (Reward.class.isAssignableFrom(extension) && extension != Reward.class)
        {
            this.baseRewards.put(identifier, extension);
        } else if (Trigger.class.isAssignableFrom(extension) && extension != Trigger.class)
        {
            return this.questPlugin.getQuestHandler().registerQuestTrigger(extension, identifier);
        } else
        {
            QuestPlugin.getLog().warning("Trying to register '" + extension.getSimpleName() + "', but cannot " +
                    "determine type (" + superClazz.getSimpleName() + ")or extended directly from CustomExtension.");
            return false;
        }

        return true;
    }

    private Map<String, Class<? extends CustomExtension>> getMap(Class<? extends CustomExtension> type)
    {
        if (type == Task.class)
        {
            return this.baseTasks;
        } else if (type == Requirement.class)
        {
            return this.baseRequirements;
        } else if (type == Reward.class)
        {
            return this.baseRewards;
        } else if (type == Trigger.class)
        {
            return this.questPlugin.getQuestHandler().getBasicTriggers();
        }

        return null;
    }

    public CustomExtension buildExtension(Class<? extends CustomExtension> type,
                                          String identifier,
                                          UUID uuid,
                                          Owner owner,
                                          Map<String, Object> configuration)
    {
        Map<String, Class<? extends CustomExtension>> map = this.getMap(type);

        // Check if map was found
        if (map == null)
        {
            QuestPlugin.getLog().severe("Could not find base map for extension type '" + type.getSimpleName() + "'");
            return null;
        }

        Class<? extends CustomExtension> extensionClazz = map.get(identifier);

        // Check if extension was found
        if (extensionClazz == null)
        {
            // This is probably some third party extension that is not yet available, will return null so the quest gets marked as
            // broken. In case the extension is registered by another plugin it will be built later anyway
            return null;
        }

        CustomExtension extension;

        // Try to get an instance
        try
        {
            extension = extensionClazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            QuestPlugin.getLog().severe("An error occurred while building a extension: ");
            e.printStackTrace();

            return null;
        }

        // Register the basic stuff
        extension.register(uuid,
                identifier,
                owner,
                this,
                this.questPlugin.getQuestHandler(),
                this.questPlugin.getPlayerHandler(),
                configuration);

        // Return the extension
        return extension;
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
            if (task.getUUID().equals(uuid))
            {
                return task;
            }
        }

        return null;
    }
}
