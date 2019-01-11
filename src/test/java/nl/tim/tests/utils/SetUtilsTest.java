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

package nl.tim.tests.utils;

import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.utils.SetUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class SetUtilsTest
{
    private Storage.DataPair<String> testPair;

    @Before
    public void setup()
    {
        testPair = new Storage.DataPair<>("test.key", "testdata1");
    }

    @Test
    public void setutils_normal_search()
    {
        Set<Storage.DataPair<String>> data = new HashSet<>();
        boolean successFlag = true;

        data.add(new Storage.DataPair<>("parent.1.test.key", "testdata1"));
        data.add(new Storage.DataPair<>("parent.2.test.key", "testdata2"));
        data.add(new Storage.DataPair<>("parent.2.test2.key", "testdata3"));

        Set<Storage.DataPair<String>> result = SetUtils.searchSetForKeys(data, "parent.1");

        for (Storage.DataPair<String> d : result)
        {
            if (!d.equals(testPair))
            {
                successFlag = false;
            }
        }

        assertTrue("The set should only contain the correct data pairs", result.size() == 1 && successFlag);
    }
}
