package nl.tim.questplugin.storage;

import com.google.inject.Inject;
import nl.tim.questplugin.storage.workers.FileStorage;
import nl.tim.questplugin.storage.workers.SQLStorage;

public class StorageProvider
{
    public enum StorageType
    {
        FILE_BASED, SQL_BASED;
    }

    private FileStorage fileStorage;
    private SQLStorage sqlStorage;

    @Inject
    public StorageProvider(FileStorage fileStorage, SQLStorage sqlStorage)
    {
        this.fileStorage = fileStorage;
        this.sqlStorage = sqlStorage;
    }

    public Storage getStorage(StorageType storageType)
    {
        switch (storageType)
        {
            case FILE_BASED:
                return this.fileStorage;
            case SQL_BASED:
                return this.sqlStorage;
            default:
                // Default to file storage
                return this.fileStorage;
        }
    }
}
