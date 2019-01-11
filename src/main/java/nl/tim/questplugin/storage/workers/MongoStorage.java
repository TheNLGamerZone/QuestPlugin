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

package nl.tim.questplugin.storage.workers;

import com.google.inject.Inject;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.storage.Storage;

import java.util.List;
import java.util.UUID;

public class MongoStorage implements Storage
{
    private QuestPlugin questPlugin;

    @Inject
    public MongoStorage(QuestPlugin questPlugin)
    {
        this.questPlugin = questPlugin;
    }

    @Override
    public boolean init() {
        QuestPlugin.getLog().warning("Mongo is not yet implemented!");
        return false;
    }

    @Override
    public void save(UUID uuid, DataType dataType, DataPair dataPair) {

    }

    @Override
    public void save(UUID uuid, DataType dataType, List<DataPair> dataPairs) {

    }

    @Override
    public void remove(UUID uuid, DataType dataType, String key)
    {

    }

    @Override
    public DataPair load(UUID uuid, DataType dataType, String key) {
        return null;
    }

    @Override
    public List<DataPair> load(UUID uuid, DataType dataType) {
        return null;
    }

    @Override
    public List<UUID> getSavedObjectsUID(DataType dataType) {
        return null;
    }
}
