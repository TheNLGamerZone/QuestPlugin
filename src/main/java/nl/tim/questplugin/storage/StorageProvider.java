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

import com.google.inject.Inject;
import nl.tim.questplugin.storage.workers.FileStorage;
import nl.tim.questplugin.storage.workers.MongoStorage;
import nl.tim.questplugin.storage.workers.SQLStorage;

public class StorageProvider
{
    public enum StorageType
    {
        FILE_BASED, SQL_BASED, MONGO_BASED,
        DEFAULT;

        public static StorageType getType(String string)
        {
            // If the string was null return a default type
            if (string == null)
            {
                return DEFAULT;
            }

            // Otherwise just return the valid type if one matches
            for (StorageType storageType : StorageType.values())
            {
                if (string.equals(storageType.name()))
                {
                    return storageType;
                }
            }

            return DEFAULT;
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
