package nl.tim.tests.storage;

import nl.tim.questplugin.utils.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
public class ConfigHandlerTest
{
    private InputStream configStream;

    @Before
    public void setup()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        this.configStream = classLoader.getResourceAsStream("config.yml");
    }

    @Test
    public void compare_config_versions()
    {
        String versionFromConstants = Constants.NEWEST_CONFIG_VERSION;
        String versionFromResource = "";

        Scanner scanner = new Scanner(this.configStream);

        while (scanner.hasNext())
        {
            String line = scanner.nextLine();

            if (line.contains(Constants.CONFIG_VERSION_OPTION))
            {
                versionFromResource = line.split(":\\s")[1];
                break;
            }
        }

        assertEquals("The config versions should be equal!", versionFromConstants, versionFromResource);
    }
}
