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

package nl.tim.questplugin.storage;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public interface Storage
{
    enum DataType
    {
        PLAYER("data/players.yml", "data/player"),
        QUEST("data/quest.yml", "data/quest"),
        AREA("data/areas.yml", "data/area"),
        REGION("data/regions.yml", "data/region"),
        EXTENSION("data/extensions.yml", "data/extension"),
        STAGE("data/stages.yml", "data/stage"),
        STAGE_CONFIG("data/stage_configurations.yml", "data/stage_config");

        private String filePath;
        private String sqlTable;

        DataType(String filePath, String sqlTable)
        {
            this.filePath = filePath;
            this.sqlTable = sqlTable;
        }

        public String getFilePath()
        {
            return this.filePath;
        }

        public String getSqlTable()
        {
            return this.sqlTable;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this)
                    .append(filePath)
                    .append(sqlTable)
                    .toString();
        }
    }

    class DataPair<V>
    {
        private String key;
        private V data;

        public DataPair(String key, V data){
            this.key = key;
            this.data = data;
        }

        public void prependKey(String key)
        {
            this.key = key + this.key;
        }

        public void appendKey(String key)
        {
            this.key = this.key + key;
        }

        public String getKey()
        {
            return this.key;
        }

        public V getData()
        {
            return this.data;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this)
                    .append(key)
                    .append(data)
                    .toString();
        }

        @Override
        public boolean equals(Object object)
        {
            if (object == this)
            {
                return true;
            }

            if (!(object instanceof DataPair))
            {
                return false;
            }

            DataPair dataPair = (DataPair) object;

            return new EqualsBuilder()
                    .append(this.key, dataPair.key)
                    .append(this.data, dataPair.data)
                    .isEquals();
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(this.key, this.data);
        }
    }

    /**
     * Setup things the selected storage type needs, like files or connections
     * @return true if storage loaded successfully, false otherwise
     */
    boolean init();

    /**
     * Will be ran on server shutdown, allows for connections to be closed etc
     */
    void close();

    /**
     * Save one the given {@link DataPair}. Can be used to save one specific piece of data.
     * @param uuid {@link UUID} of the object
     * @param dataType {@link DataType} of object
     * @param dataPair {@link DataPair} to save
     */
    void save(UUID uuid, DataType dataType, DataPair<String> dataPair);

    /**
     * Save multiple {@link DataPair}s.
     * @param uuid {@link UUID} of object
     * @param dataType {@link DataType} of the object to save
     * @param dataPairs {@link DataPair}s to save
     */
    void save(UUID uuid, DataType dataType, Collection<DataPair<String>> dataPairs);

    /**
     * Removes data with given key
     * @param uuid {@link UUID} of object
     * @param dataType {@link DataType} of object
     * @param key key to search for
     */
    void remove(UUID uuid, DataType dataType, String key);

    /**
     * Load one piece of saved data with the given {@link UUID} and key.
     * @param uuid {@link UUID} of the object to search for
     * @param dataType {@link DataType} of required data
     * @param key key to of the required data
     * @return {@link DataPair} containing data or null if nothing was found.
     */
    DataPair load(UUID uuid, DataType dataType, String key);

    /**
     * Loads all data of a given object with given UUID.
     * @param uuid {@link UUID} of the object to load
     * @param dataType {@link DataType} of the objet
     * @return {@link List<DataPair>} containing all data of the object (will be empty when nothing was found).
     */
    List<DataPair<String>> load(UUID uuid, DataType dataType);

    /**
     * Returns all UUID saved of the given {@link DataType}.
     * @param dataType {@link DataType} to search for
     * @return {@link List<UUID>} containing all found UUIDs of the given {@link DataType}.
     */
    List<UUID> getSavedObjectsUID(DataType dataType);
}
