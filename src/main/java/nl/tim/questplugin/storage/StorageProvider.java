package nl.tim.questplugin.storage;

import com.google.inject.Inject;
import nl.tim.questplugin.storage.workers.FileStorage;
import nl.tim.questplugin.storage.workers.MongoStorage;
import nl.tim.questplugin.storage.workers.SQLStorage;

public class StorageProvider
{
    public enum StorageType
    {
        FILE_BASED, SQL_BASED, MONGO_BASED;

        public static StorageType getType(String string)
        {
            if (string == null)
            {
                return null;
            }

            for (StorageType storageType : StorageType.values())
            {
                if (string.equals(storageType.name()))
                {
                    return storageType;
                }
            }

            return null;
        }
    }

    private FileStorage fileStorage;
    private SQLStorage sqlStorage;
    private MongoStorage mongoStorage;

    @Inject
    public StorageProvider(FileStorage fileStorage, SQLStorage sqlStorage, MongoStorage mongoStorage)
    {
        this.fileStorage = fileStorage;
        this.sqlStorage = sqlStorage;
        this.mongoStorage = mongoStorage;
    }

    public Storage getStorage(StorageType storageType)
    {
        // Return correct storage instance based on input
        switch (storageType)
        {
            case FILE_BASED:
                return this.fileStorage;
            case SQL_BASED:
                return this.sqlStorage;
            case MONGO_BASED:
                return this.mongoStorage;
            default:
                // Default to file storage
                return this.fileStorage;
        }
    }
}
