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

package nl.tim.questplugin.storage.image.builders;

import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.quest.ExtensionType;
import nl.tim.questplugin.quest.CustomExtension;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.StorageProvider;
import nl.tim.questplugin.storage.image.ImageBuilder;
import nl.tim.questplugin.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class ExtensionImageBuilder implements ImageBuilder<CustomExtension>
{
    private QuestPlugin questPlugin;
    private Storage storage;

    @Inject
    public ExtensionImageBuilder(QuestPlugin questPlugin, StorageProvider storageProvider)
    {
        this.questPlugin = questPlugin;
        this.storage = storageProvider.getStorage(QuestPlugin.storageType);
    }

    @Override
    public void save(CustomExtension customExtension)
    {
        // Save extension
        this.storage.save(customExtension.getUUID(), Storage.DataType.EXTENSION, customExtension.getData());
    }

    @Override
    public CustomExtension load(UUID uuid)
    {
        List<Storage.DataPair<String>> dataPairs = this.storage.load(uuid, Storage.DataType.EXTENSION);

        // Determine extension type first
        String type = null;
        String id = null;
        UUID ownerUUID = null;
        Map<String, Object> config = new HashMap<>();

        for (Storage.DataPair<String> dataPair : dataPairs)
        {
            String key = dataPair.getKey();
            String data = dataPair.getData();

            if (key.equals("type"))
            {
                type = data;
            } else if (key.equals("id"))
            {
                id = data;
            } else if (key.equals("owner") && StringUtils.isUUID(data))
            {
                ownerUUID = UUID.fromString(data);
            } else if (key.contains("configuration"))
            {
                config.put(key, data);
            } else
            {
                QuestPlugin.getLog().warning("Unknown or invalid extension data found: '" + key + "'->'" + data + "'");
            }
        }

        // Check if any required data was not found
        if (type == null || id == null || ownerUUID == null)
        {
            QuestPlugin.getLog().warning("Could not load all required information for extension '" + uuid + "'");
            return null;
        }

        ExtensionType extensionType = ExtensionType.get(type);

        // Check if the extension type is invalid
        if (extensionType == null)
        {
            QuestPlugin.getLog().warning("Could not load extension of type '" + type + "'");
            return null;
        }

        return this.questPlugin.getTaskHandler().buildExtension(extensionType.getClazz(), id, uuid, ownerUUID, config);
    }
}
