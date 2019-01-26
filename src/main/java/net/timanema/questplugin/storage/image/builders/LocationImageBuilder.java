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

import com.google.inject.Inject;
import net.timanema.questplugin.QuestPlugin;
import net.timanema.questplugin.storage.Storage;
import net.timanema.questplugin.storage.StorageProvider;
import net.timanema.questplugin.storage.image.ImageBuilder;
import net.timanema.questplugin.utils.LocationWithID;
import net.timanema.questplugin.utils.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocationImageBuilder implements ImageBuilder<LocationWithID>
{
    private QuestPlugin questPlugin;
    private Storage storage;

    @Inject
    public LocationImageBuilder(QuestPlugin questPlugin, StorageProvider storageProvider)
    {
        this.questPlugin = questPlugin;
        this.storage = storageProvider.getStorage(QuestPlugin.storageType);
    }

    @Override
    public void save(LocationWithID location)
    {
        /*
        Format will be like this:
        <location_uuid>:
            world: <world_uuid>
            x: <x>
            y: <y>
            z: <z>
            pitch: <pitch>
            yaw: <yaw>
         */

        List<Storage.DataPair> result = new ArrayList<>(6);

        result.add(new Storage.DataPair<>("world", location.getWorld().getUID().toString()));
        result.add(new Storage.DataPair<>("x", location.getX()));
        result.add(new Storage.DataPair<>("y", location.getY()));
        result.add(new Storage.DataPair<>("z", location.getZ()));
        result.add(new Storage.DataPair<>("pitch", location.getPitch()));
        result.add(new Storage.DataPair<>("yaw", location.getYaw()));

        this.storage.save(location.getUUID(), Storage.DataType.LOCATION, result);
    }

    @Override
    public LocationWithID load(UUID uuid)
    {
        List<Storage.DataPair> dataPairs = this.storage.load(uuid, Storage.DataType.LOCATION);
        World world = null;
        double x = 0;
        double y = 0;
        double z = 0;
        float pitch = 0;
        float yaw = 0;

        for (Storage.DataPair dataPair : dataPairs)
        {
            String key = dataPair.getKey();
            String data = dataPair.getData().toString();

            switch (key)
            {
                case "world":
                    if (StringUtils.isUUID(data))
                    {
                        world = this.questPlugin.getServer().getWorld(UUID.fromString(data));
                    }
                    break;
                case "x":
                    if (NumberUtils.isNumber(data))
                    {
                        x = Double.parseDouble(data);
                    }
                    break;
                case "y":
                    if (NumberUtils.isNumber(data))
                    {
                        y = Double.parseDouble(data);
                    }
                    break;
                case "z":
                    if (NumberUtils.isNumber(data))
                    {
                        z = Double.parseDouble(data);
                    }
                    break;
                case "pitch":
                    if (NumberUtils.isNumber(data))
                    {
                        pitch = Float.parseFloat(data);
                    }
                    break;
                case "yaw":
                    if (NumberUtils.isNumber(data))
                    {
                        yaw = Float.parseFloat(data);
                    }
                    break;
                default:
                    QuestPlugin.getLog().warning("Unknown data type for location: " + key);
                    break;
            }
        }

        if (world == null)
        {
            QuestPlugin.getLog().warning("Unable to load world for location " + uuid);
            return null;
        }

        return new LocationWithID(uuid, world, x, y, z, pitch, yaw);
    }
}
