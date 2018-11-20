package nl.tim.tests.utils;

import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.utils.SetUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
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
