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

import nl.tim.questplugin.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class StorageTest
{
    private Storage.DataPair<String> testPair;
    private Storage.DataPair<String> otherPair;

    @Before
    public void setup()
    {
        testPair = new Storage.DataPair<>("test.key", "testdata");
    }

    @Test
    public void datapair_get_key()
    {
        assertEquals("Key should be equal to test.key", testPair.getKey(), "test.key");
    }

    @Test
    public void datapair_get_data()
    {
        assertEquals("Data should be equal to testdata", testPair.getData(), "testdata");
    }

    @Test
    public void datapair_equals_same()
    {
        assertEquals("A datapair is equal to itself!", testPair, testPair);
    }

    @Test
    public void datapair_equals_other_type()
    {
        assertFalse("A datapair is not equal to a string!", testPair.equals(new String()));
    }

    @Test
    public void datapair_equals_not_equal_key()
    {
        otherPair = new Storage.DataPair<>("other.key", "testdata");

        assertFalse("The key values were not equal!", testPair.equals(otherPair));
    }

    @Test
    public void datapair_equals_not_equal_data()
    {
        otherPair = new Storage.DataPair<>("test.key", "otherdata");

        assertFalse("The data values were not equal!", testPair.equals(otherPair));
    }

    @Test
    public void datapair_equals_equal()
    {
        otherPair = new Storage.DataPair<>("test.key", "testdata");

        assertEquals("The pairs are equal!", testPair, otherPair);
    }

    @Test
    public void datapair_hashcode()
    {
        otherPair = new Storage.DataPair<>("test.key", "testdata");

        assertEquals("Hashcodes of equal objects should be equal too!", testPair.hashCode(), otherPair.hashCode());
    }

    @Test
    public void datatype_get_filepath()
    {
        assertEquals("getFilePath() should return the correct filepath!", Storage.DataType.PLAYER.getFilePath(), "data/players.yml");
    }

    @Test
    public void datatype_get_sqltable()
    {
        assertEquals("getSqlTable() should return the correct filepath!", Storage.DataType.AREA.getSqlTable(), "data/area");
    }
}
