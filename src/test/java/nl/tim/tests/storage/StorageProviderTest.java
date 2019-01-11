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
