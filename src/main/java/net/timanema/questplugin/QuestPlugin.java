/*
 * Copyright (C) 2019  Tim Anema
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.timanema.questplugin;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.timanema.questplugin.quest.stage.requirements.NameRequirement;
import net.timanema.questplugin.quest.stage.rewards.MessageReward;
import net.timanema.questplugin.quest.tasks.DummyTask;
import net.timanema.questplugin.quest.triggers.AreaTrigger;
import net.timanema.questplugin.storage.Storage;
import net.timanema.questplugin.storage.StorageProvider;
import net.timanema.questplugin.storage.image.builders.*;
import net.timanema.questplugin.utils.Constants;
import net.timanema.questplugin.player.PlayerHandler;
import net.timanema.questplugin.quest.QuestHandler;
import net.timanema.questplugin.quest.TaskHandler;
import net.timanema.questplugin.storage.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class QuestPlugin extends JavaPlugin
{
    public static StorageProvider.StorageType storageType;

    private static Logger logger;
    private Injector injector;

    @Inject private StorageProvider storageProvider;
    private Storage storage;
    private boolean storageLoaded = false;

    private ConfigHandler configHandler;

    // TODO: Maybe move these around some time, the amount of image builders keeps growing :(
    @Inject private AreaImageBuilder areaImageBuilder;
    @Inject private QuestImageBuilder questImageBuilder;
    @Inject private PlayerImageBuilder playerImageBuilder;
    @Inject private RegionImageBuilder regionImageBuilder;
    @Inject private StageImageBuilder stageImageBuilder;
    @Inject private StageConfigurationImageBuilder stageConfigurationImageBuilder;
    @Inject private ExtensionImageBuilder extensionImageBuilder;
    @Inject private LocationImageBuilder locationImageBuilder;

    @Inject private TaskHandler taskHandler;
    @Inject private QuestHandler questHandler;
    @Inject private PlayerHandler playerHandler;

    @Override
    public void onEnable() {
        logger = getLogger();

        // Enable storage
        this.configHandler = new ConfigHandler(this);

        this.configHandler.init();
        String storageTypeResult = this.configHandler.getOption(String.class, Constants.STORAGE_OPTION);

        // Determining storage type for other data
        storageType = StorageProvider.StorageType.getType(storageTypeResult);

        if (storageType == null)
        {
            logger.warning("Unsupported storage type in config.yml: " + storageTypeResult);
            logger.warning("Will switch to default: FILE_BASED");

            storageType = StorageProvider.StorageType.FILE_BASED;
        }

        logger.info("Will use storage type: " + storageType.name());

        // Running DI
        logger.info("Running dependency injector");

        QuestBinder questBinder = new QuestBinder(this, getDataFolder());
        this.injector = questBinder.createInjector();

        // Inject all classes
        this.injector.injectMembers(this);

        // Continue normally
        logger.info("Loading handlers");

        this.storage = this.storageProvider.getStorage(storageType);
        this.storageLoaded = this.storage.init();

        // Check if storage loaded properly, otherwise disable plugin
        if (!this.storageLoaded)
        {
            logger.severe("Storage was not properly loaded, disabling plugin!");
            Bukkit.getPluginManager().disablePlugin(this);

            return;
        }

        // Continue loading if storage was properly initialized
        //TODO: Make this into a method later on
        this.taskHandler.registerCustomExtension(DummyTask.class);
        this.taskHandler.registerCustomExtension(MessageReward.class);
        this.taskHandler.registerCustomExtension(NameRequirement.class);
        this.taskHandler.registerCustomExtension(AreaTrigger.class);

        //TODO: Load quests here, maybe use a runnable to run quest init after third party plugins had the chance to register stuff?
        //TODO: Fix temp loading situation
        this.questHandler.initQuests(this.storage.getSavedObjectsUID(Storage.DataType.QUEST));
        this.questHandler.initFloatingStages(this.storage.getSavedObjectsUID(Storage.DataType.STAGE));

        // Done with loading
        logger.info("QuestPlugin is enabled!");
    }

    @Override
    public void onDisable() {
        // Check if we have to unload anything
        if (!this.storageLoaded)
        {
            // Storage wasn't loading, therefore nothing else is loaded
            return;
        }

        // Save data
        //TODO: Implement

        // Close connections if any
        this.storage.close();

        // Continue normal disabling
        logger.info("QuestPlugin is disabled!");
    }

    public Injector getInjector()
    {
        return this.injector;
    }

    public Storage getStorage()
    {
        return this.storage;
    }

    public ConfigHandler getConfigHandler()
    {
        return this.configHandler;
    }

    public static Logger getLog()
    {
        return logger;
    }

    //TODO: Yep definitely have to move these around in the near future
    public TaskHandler getTaskHandler()
    {
        return this.taskHandler;
    }

    public QuestHandler getQuestHandler()
    {
        return this.questHandler;
    }

    public PlayerHandler getPlayerHandler()
    {
        return this.playerHandler;
    }

    public QuestImageBuilder getQuestImageBuilder()
    {
        return this.questImageBuilder;
    }

    public AreaImageBuilder getAreaImageBuilder()
    {
        return this.areaImageBuilder;
    }

    public RegionImageBuilder getRegionImageBuilder()
    {
        return this.regionImageBuilder;
    }

    public StageImageBuilder getStageImageBuilder()
    {
        return this.stageImageBuilder;
    }

    public StageConfigurationImageBuilder getStageConfigurationImageBuilder()
    {
        return this.stageConfigurationImageBuilder;
    }

    public ExtensionImageBuilder getExtensionImageBuilder()
    {
        return this.extensionImageBuilder;
    }

    public LocationImageBuilder getLocationImageBuilder()
    {
        return this.locationImageBuilder;
    }
}
