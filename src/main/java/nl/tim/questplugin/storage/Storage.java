package nl.tim.questplugin.storage;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

public interface Storage
{
    enum DataType
    {
        PLAYER("data/players.yml", "data/player"),
        QUEST("data/quest.yml", "data/quest"),
        AREA("data/areas.yml", "data/area"),
        REGION("data/regions.yml", "data/region");

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

    class DataPair<K extends String, V>
    {
        private K key;
        private V data;

        public DataPair(K key, V data){
            this.key = key;
            this.data = data;
        }

        public K getKey()
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
    }

    /**
     * Setup things the selected storage type needs, like files or connections
     */
    void init();

    /**
     * Save one DataPair
     * Can be used to save one specific piece of data
     */
    void save(UUID uuid, DataType dataType, DataPair dataPair);

    /**
     * Save multiple DataPairs
     * Can be used to save all data of an object with given UUID
     */
    void save(UUID uuid, DataType dataType, DataPair[] dataPairs);

    /**
     * Load one piece of saved data
     */
    DataPair load(UUID uuid, DataType dataType, String key);

    /**
     * Load all data of a given object with given UUID
     */
    DataPair[] load(UUID uuid, DataType dataType);

    /**
     * Can be used to get all uuids of objects saved of the given DataType
     */
    UUID[] getSavedObjectsUID(DataType dataType);
}
