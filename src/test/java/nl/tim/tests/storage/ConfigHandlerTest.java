package nl.tim.tests.storage;

import com.sun.org.apache.xpath.internal.operations.Bool;
import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.storage.ConfigHandler;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.utils.Constants;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "nl.tim.questplugin.*")
public class ConfigHandlerTest
{
    private QuestPlugin mockPlugin;
    private FileConfiguration mockConfig;
    private ConfigHandler configHandler;
    private Logger mockLogger;

    @Before
    public void setup()
    {
        // Mock plugin class
        PowerMockito.mockStatic(QuestPlugin.class);
        mockPlugin = PowerMockito.mock(QuestPlugin.class);
        mockConfig = PowerMockito.mock(FileConfiguration.class);
        mockLogger = PowerMockito.mock(Logger.class);

        //PowerMockito.doNothing().when(mockPlugin).saveResource(any(String.class), any(Boolean.class));
        suppress(method(QuestPlugin.class, "saveResource"));
        suppress(method(QuestPlugin.class, "getDataFolder"));//when(mockPlugin.getDataFolder()).thenReturn(null);

        // Setup logger
        when(QuestPlugin.getLog()).thenReturn(mockLogger);
        PowerMockito.doNothing().when(mockLogger).info(any(String.class));
        PowerMockito.doNothing().when(mockLogger).warning(any(String.class));

        // Setup fake config file
        when(mockPlugin.getConfig()).thenReturn(mockConfig);
        when(mockConfig.isSet(any(String.class))).thenReturn(true);

        // Setup config handler
        configHandler = new ConfigHandler(mockPlugin);
        //mockConfigHandler.init();

        // Create static mocks for updating files
        PowerMockito.mockStatic(Paths.class);
        PowerMockito.mockStatic(Files.class);
    }

    /* Testing method: ConfigHandler#getOption() */
    @Test
    public void getoption_string()
    {
        when(mockConfig.getString("test")).thenReturn("result");

        String result = configHandler.getOption(String.class, "test");

        assertEquals("Result should be a string and of value 'result'", "result", result);
    }

    @Test
    public void getoption_boolean()
    {
        when(mockConfig.getString("test")).thenReturn("true");

        boolean result = configHandler.getOption(Boolean.class, "test");

        assertTrue("Result should be a boolean and of value 'true'", result);
    }

    @Test
    public void getoption_int()
    {
        when(mockConfig.getString("test")).thenReturn("42");

        int result = configHandler.getOption(Integer.class, "test");

        assertEquals("Result should be a integer and of value '42'", 42, result);
    }

    @Test
    public void getoption_long()
    {
        when(mockConfig.getString("test")).thenReturn("4242");

        long result = configHandler.getOption(Long.class, "test");

        assertEquals("Result should be a long and of value '4242'", 4242, result);
    }

    @Test
    public void getoption_double()
    {
        when(mockConfig.getString("test")).thenReturn("42.42");

        double result = configHandler.getOption(Double.class, "test");

        Assert.assertEquals("Result should be a double and of value '42.42'", result, 42.42, 0.0002);
    }

    @Test
    public void getoption_null()
    {
        when(mockConfig.getString("test")).thenReturn(null);

        String result = configHandler.getOption(String.class, "test");

        assertNull("Result should be a null", result);
    }

    /* Test methods: updating configuration related */
    @Test
    public void compareversion_up_to_date()
    {
        when(mockConfig.getString("config_version")).thenReturn("MOCK-VERSION-42-101");

        assertTrue(configHandler.compareVersions("MOCK-VERSION-42-101"));
    }

    @Test
    public void compareversion_outdated()
    {
        when(mockConfig.getString("config_version")).thenReturn("MOCK-VERSION-42-101");

        assertFalse(configHandler.compareVersions("MOCK-VERSION-42-102"));
    }

    @Test
    public void updateconfig_up_to_date()
    {
        when(mockConfig.getString("config_version")).thenReturn("MOCK-VERSION-42-101");

        assertFalse("Config is up-to-date and shouldn't be updated!", configHandler.updateConfig("MOCK-VERSION-42-101"));
    }

    @Test
    public void updateconfig_outdated()
    {
        // Mock config version
        when(mockConfig.getString("config_version")).thenReturn("MOCK-VERSION-42-101");

        try
        {
            // Mocking old config file
            Set<String> mockKeys = new HashSet<>();
            List<String> expected = new ArrayList<>();

            mockKeys.add("configOption1");
            mockKeys.add("configOption2");
            mockKeys.add("configOption3");

            when(mockConfig.getKeys(false)).thenReturn(mockKeys);
            when(mockConfig.getString("configOption1")).thenReturn("old_setting");
            when(mockConfig.getString("configOption2")).thenReturn("old_setting");
            when(mockConfig.getString("configOption3")).thenReturn("old_setting");

            List<String> mockLines = new ArrayList<>();
            mockLines.add("# comment");
            mockLines.add("configOption1: new_setting");
            mockLines.add("configOption2: new_setting");
            mockLines.add("configOption3: old_setting");
            mockLines.add("configOption4: new_setting");

            //suppress(method(Paths.class, "get", anyString(), Matchers.<String>any()));//, String.class));
            when(Files.readAllLines(any(Path.class))).thenReturn(mockLines);

            // Run method
            boolean result = configHandler.updateConfig("MOCK-VERSION-42-102");

            // Verify result
            final ArgumentCaptor<ArrayList> captor = ArgumentCaptor.forClass(ArrayList.class);

            verifyStatic();
            Files.write(Matchers.any(Path.class), captor.capture());

            // Creating expected 'file'
            expected.add("# comment");
            expected.add("configOption1: old_setting");
            expected.add("configOption2: old_setting");
            expected.add("configOption3: old_setting");
            expected.add("configOption4: new_setting");

            assertTrue("Updating should return true if finished!", result);
            assertEquals("The update should modify the new config file correctly!", expected, captor.getValue());
        } catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void compare_config_versions()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream configStream = classLoader.getResourceAsStream("config.yml");

        String versionFromConstants = Constants.NEWEST_CONFIG_VERSION;
        String versionFromResource = "";

        Scanner scanner = new Scanner(configStream);

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
