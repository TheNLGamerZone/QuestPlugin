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

package net.timanema.questplugin.storage.image.builders;

import net.timanema.questplugin.QuestPlugin;
import net.timanema.questplugin.storage.Storage;
import net.timanema.questplugin.storage.StorageProvider;
import net.timanema.questplugin.quest.CustomExtension;
import net.timanema.questplugin.quest.stage.Stage;
import net.timanema.questplugin.quest.stage.StageConfiguration;
import net.timanema.questplugin.storage.image.ImageBuilder;
import net.timanema.questplugin.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Singleton
public class StageImageBuilder implements ImageBuilder<Stage>
{
    private QuestPlugin questPlugin;
    private Storage storage;

    @Inject
    public StageImageBuilder(QuestPlugin questPlugin, StorageProvider storageProvider)
    {
        this.questPlugin = questPlugin;
        this.storage = storageProvider.getStorage(QuestPlugin.storageType);
    }

    @Override
    public void save(Stage stage)
    {
        if (stage == null)
        {
            return;
        }

        /*
        Format will be like this:
        <stage_uuid>:
            id: <identifier>
            quest: <quest_uuid>
         */
        List<Storage.DataPair> dataPairs = new ArrayList<>();

        // Add ID and parent
        dataPairs.add(new Storage.DataPair<>("id", stage.getIdentifier()));
        dataPairs.add(new Storage.DataPair<>("quest", stage.getQuest() + ""));

        // Save stage config
        this.questPlugin.getStageConfigurationImageBuilder().save(stage.getConfiguration());

        // Save stage
        this.storage.save(stage.getUUID(), Storage.DataType.STAGE, dataPairs);
    }

    @Override
    public Stage load(UUID uuid)
    {
        List<Storage.DataPair> dataPairs = this.storage.load(uuid, Storage.DataType.STAGE);
        String id = null;
        UUID quest = null;

        if (dataPairs == null)
        {
            return null;
        }

        // Loading data
        for (Storage.DataPair dataPair : dataPairs)
        {
            String data = dataPair.getData().toString();

            switch (dataPair.getKey())
            {
                case "id":
                    id = data;
                    break;
                case "quest":
                    if (data != null && !data.equalsIgnoreCase("null") && StringUtils.isUUID(data))
                    {
                        quest = UUID.fromString(data);
                        break;
                    }

                    QuestPlugin.getLog().info("Stage '" + uuid + "' loaded as a floating stage!");
                    break;
                 default:
                     QuestPlugin.getLog().warning("Unknown data type for stage '" + dataPair.getKey() + "'");
                     break;
            }
        }

        // Load stage configuration
        StageConfiguration stageConfiguration = this.questPlugin.getStageConfigurationImageBuilder().load(uuid);

        // Check if anything was not loaded
        if (id == null || stageConfiguration == null)
        {
            // Log and return broken stage
            QuestPlugin.getLog().warning("Stage failed to load: stage configuration/id/parent could not be loaded!");
            return null;
        }

        // Create stage
        Stage stage = new Stage(id, quest, uuid, stageConfiguration, false, false, false);

        // Create list of extensions
        List<CustomExtension> extensions = new ArrayList<>(stage.getConfiguration().getRewards());

        extensions.addAll(stage.getConfiguration().getTasks());
        extensions.addAll(stage.getConfiguration().getStageRewards());
        extensions.addAll(stage.getConfiguration().getStageStartRewards());

        // Register stage as owner
        extensions.forEach(ext -> ext.registerOwner(stage));
        stage.getConfiguration().getRequirements().registerOwner(stage);

        // Update flags
        stage.checkBroken();
        stage.checkBranching();
        stage.checkBranchingTasks();

        return stage;
    }
}
