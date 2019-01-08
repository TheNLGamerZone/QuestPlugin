package nl.tim.tests.storage;

import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.storage.ConfigHandler;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "nl.tim.questplugin.*")
public class ConfigHandlerTest
{
    private FileConfiguration mockConfig;
    private ConfigHandler configHandler;

    @Before
    public void setup()
    {
        // Mock plugin class
        PowerMockito.mockStatic(QuestPlugin.class);
        QuestPlugin mockPlugin = PowerMockito.mock(QuestPlugin.class);
        mockConfig = PowerMockito.mock(FileConfiguration.class);
        Logger mockLogger = PowerMockito.mock(Logger.class);

        // Suppress these methods (just to be safe)
        suppress(method(QuestPlugin.class, "saveResource"));
        suppress(method(QuestPlugin.class, "getDataFolder"));

        // Setup config file responses
        when(mockConfig.getString("test1")).thenReturn("result");
        when(mockConfig.getString("test2")).thenReturn("true");
        when(mockConfig.getString("test3")).thenReturn("42");
        when(mockConfig.getString("test4")).thenReturn("4242");
        when(mockConfig.getString("test5")).thenReturn("42.42");
        when(mockConfig.getString("test6")).thenReturn(null);
        when(mockConfig.getString("config_version")).thenReturn("MOCK-VERSION-42-101");

        // Setup logger
        when(QuestPlugin.getLog()).thenReturn(mockLogger);
        PowerMockito.doNothing().when(mockLogger).info(any(String.class));
        PowerMockito.doNothing().when(mockLogger).warning(any(String.class));

        // Setup fake config file
        when(mockPlugin.getConfig()).thenReturn(mockConfig);
        when(mockConfig.isSet(Matchers.eq("valueNotSet"))).thenReturn(false);
        when(mockConfig.isSet(AdditionalMatchers.not(Matchers.eq("valueNotSet")))).thenReturn(true);

        // Setup config handler
        configHandler = new ConfigHandler(mockPlugin);


        // Create static mocks for updating files
        PowerMockito.mockStatic(Paths.class);
        PowerMockito.mockStatic(Files.class);
    }

    /* Testing method: ConfigHandler#getOption() */
    @Test
    public void getoption_string()
    {
        String result = configHandler.getOption(String.class, "test1");

        assertEquals("Result should be a string and of value 'result'", "result", result);
    }

    @Test
    public void getoption_boolean()
    {
        boolean result = configHandler.getOption(Boolean.class, "test2");

        assertTrue("Result should be a boolean and of value 'true'", result);
    }

    @Test
    public void getoption_int()
    {
        int result = configHandler.getOption(Integer.class, "test3");

        assertEquals("Result should be a integer and of value '42'", 42, result);
    }

    @Test
    public void getoption_long()
    {
        long result = configHandler.getOption(Long.class, "test4");

        assertEquals("Result should be a long and of value '4242'", 4242, result);
    }

    @Test
    public void getoption_double()
    {
        double result = configHandler.getOption(Double.class, "test5");

        assertEquals("Result should be a double and of value '42.42'", result, 42.42, 0.0002);
    }

    @Test
    public void getoption_null()
    {
        String result = configHandler.getOption(String.class, "test6");

        assertNull("Result should be a null since the original value was also null", result);
    }

    @Test
    public void getoption_not_set()
    {
        String result = configHandler.getOption(String.class, "valueNotSet");

        assertNull("Result should be a null since the requested key did not exist in the file", result);
    }

    /* Testing methods: updating configuration related */
    @Test
    public void compareversion_up_to_date()
    {
        assertTrue(configHandler.compareVersions("MOCK-VERSION-42-101"));
    }

    @Test
    public void compareversion_outdated()
    {
        assertFalse(configHandler.compareVersions("MOCK-VERSION-42-102"));
    }

    @Test
    public void updateconfig_up_to_date()
    {
        assertFalse("Config is up-to-date and shouldn't be updated!", configHandler.updateConfig("MOCK-VERSION-42-101"));
    }

    @Test
    public void updateconfig_outdated()
    {
        try
        {
            // Mocking old config file
            Set<String> mockKeys = new HashSet<>();
            List<String> expected = new ArrayList<>();

            mockKeys.add("config_version");
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

            when(Files.readAllLines(any(Path.class))).thenReturn(mockLines);

            // Run method
            boolean result = configHandler.updateConfig("MOCK-VERSION-42-102");

            // Verify result
            final ArgumentCaptor<ArrayList> captor = ArgumentCaptor.forClass(ArrayList.class);

            verifyStatic();
            Files.write(any(Path.class), captor.capture());

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

    /* Testing method: ConfigHandler#createFileIfNotExists */
    @Test
    public void create_file_exists()
    {
        // Mock file
        File mockFile = PowerMockito.mock(File.class);
        File parentFile = PowerMockito.mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.getParentFile()).thenReturn(parentFile);

        // 'Create' it
        ConfigHandler.createFileIfNotExists(mockFile);

        // Check the mkdirs method was never called
        verify(parentFile, times(0)).mkdirs();
    }

    @Test
    public void create_file_not_exists()
    {
        // Mock file
        File mockFile = PowerMockito.mock(File.class);
        File parentFile = PowerMockito.mock(File.class);
        when(mockFile.exists()).thenReturn(false);
        when(mockFile.getParentFile()).thenReturn(parentFile);

        // 'Create' it
        ConfigHandler.createFileIfNotExists(mockFile);

        try
        {
            // Check the createNewFile method was called once
            verify(mockFile, times(1)).createNewFile();
        } catch (IOException e)
        {
            e.printStackTrace();
            fail("Exception that should not happen");
        }
    }

    // Making sure the config version is correct
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
