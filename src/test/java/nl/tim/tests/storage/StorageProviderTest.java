package nl.tim.tests.storage;

import nl.tim.questplugin.storage.StorageProvider;
import nl.tim.questplugin.storage.workers.FileStorage;
import nl.tim.questplugin.storage.workers.MongoStorage;
import nl.tim.questplugin.storage.workers.SQLStorage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class StorageProviderTest
{
    private StorageProvider storageProvider;

    @Before
    public void setup()
    {
        FileStorage mockFileStorage = PowerMockito.mock(FileStorage.class);
        SQLStorage mockSQLStorage = PowerMockito.mock(SQLStorage.class);
        MongoStorage mockMongoStorage = PowerMockito.mock(MongoStorage.class);

        storageProvider = new StorageProvider(mockFileStorage, mockSQLStorage, mockMongoStorage);
    }

    @Test
    public void mongo_provider()
    {
        assertTrue("Storage provider should return a mongo provider when asked to!",
                storageProvider.getStorage(StorageProvider.StorageType.MONGO_BASED) instanceof MongoStorage);
    }

    @Test
    public void sql_provider()
    {
        assertTrue("Storage provider should return a SQL provider when asked to!",
                storageProvider.getStorage(StorageProvider.StorageType.SQL_BASED) instanceof SQLStorage);
    }

    @Test
    public void file_provider()
    {
        assertTrue("Storage provider should return a file provider when asked to!",
                storageProvider.getStorage(StorageProvider.StorageType.FILE_BASED) instanceof FileStorage);
    }

    @Test
    public void default_provider()
    {
        assertTrue("Storage provider should return a file provider by default!",
                storageProvider.getStorage(StorageProvider.StorageType.getType("ThisIsNotValid")) instanceof FileStorage);
    }
}
