package nl.tim.questplugin.storage;

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
    }

    class DataPair<K extends String, V>
    {
        private K key;
        private V data;

        public DataPair(K key, V data){
            this.key = key;
            this.data = data;
        }

        K getKey()
        {
            return this.key;
        }

        V getData()
        {
            return this.data;
        }
    }

    void init();

    void save(UUID uuid, DataType dataType, DataPair dataPair);

    void save(UUID uuid, DataType dataType, DataPair[] dataPairs);

    DataPair load(UUID uuid, DataType dataType, String key);

    DataPair[] load(UUID uuid, DataType dataType);
}
