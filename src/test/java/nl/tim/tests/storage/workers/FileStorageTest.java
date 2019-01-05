package nl.tim.tests.storage.workers;

import nl.tim.questplugin.QuestPlugin;
import nl.tim.questplugin.storage.ConfigHandler;
import nl.tim.questplugin.storage.Storage;
import nl.tim.questplugin.storage.workers.FileStorage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "nl.tim.questplugin.*")
public class FileStorageTest
{
    private final int NUMBER_OF_FILES = 4;
    private FileStorage fileStorage;

    @Before
    public void setup()
    {
        // Create mocks
        QuestPlugin mockPlugin = PowerMockito.mock(QuestPlugin.class);
        File mockStorageLocation = PowerMockito.mock(File.class);
        Logger mockLogger = PowerMockito.mock(Logger.class);

        // Make a static mock so we can 'create' files
        PowerMockito.mockStatic(ConfigHandler.class);

        // Setup logger
        PowerMockito.mockStatic(QuestPlugin.class);
        when(QuestPlugin.getLog()).thenReturn(mockLogger);
        PowerMockito.doNothing().when(mockLogger).info(any(String.class));
        PowerMockito.doNothing().when(mockLogger).warning(any(String.class));

        // Init file storage
        fileStorage = new FileStorage(mockPlugin, mockStorageLocation);
    }

    @Test
    public void init_check_files()
    {
        // Init file storage
        fileStorage.init();

        // Check if all files were verified
        PowerMockito.verifyStatic(times(NUMBER_OF_FILES));
        ConfigHandler.createFileIfNotExists(any(File.class));
    }

    @Test
    public void save()
    {
        try
        {
            // Create mock data file and file configuration
            File mockFile = PowerMockito.mock(File.class);
            YamlConfiguration mockFileConfiguration = PowerMockito.mock(YamlConfiguration.class);

            // Set file path of mock data file
            when(mockFile.toString()).thenReturn("NOT");

            // Intercept creation of real file, return the fake file instead
            PowerMockito.whenNew(File.class)
                    .withParameterTypes(String.class)
                    .withArguments(Matchers.eq("NOT" + File.separator + "data" + File.separator + "players.yml"))
                    .thenReturn(mockFile);

            // Intercept creation of file configuration
            PowerMockito.whenNew(YamlConfiguration.class).withNoArguments().thenReturn(mockFileConfiguration);

            // Call save method
            UUID uuid = UUID.randomUUID();
            List<Storage.DataPair> dataPairs = new ArrayList<>();
            dataPairs.add(new Storage.DataPair<>("key1", "data1"));
            dataPairs.add(new Storage.DataPair<>("key2", "data2"));
            dataPairs.add(new Storage.DataPair<>("key3", "data3"));
            dataPairs.add(null);

            fileStorage.save(uuid, Storage.DataType.PLAYER, dataPairs);

            // Verify calls made
            verify(mockFileConfiguration, times(1)).load(any(File.class));
            verify(mockFileConfiguration, times(1)).save(any(File.class));

            // Verify actual arguments to file configuration
            ArgumentCaptor<String> keys = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> data = ArgumentCaptor.forClass(String.class);

            verify(mockFileConfiguration, atLeastOnce()).set(keys.capture(), data.capture());

            // First check if the amount of passed args is as we expected
            // -1 because we do not expect the first null value to be passed
            if (keys.getAllValues().size() != dataPairs.size() - 1 ||
                    keys.getAllValues().size() != data.getAllValues().size())
            {
                fail("Not all arguments were passed!");
                return;
            }

            // Loop through all values passed to the set() and verify them
            // Exclude the first value of the array, because that's null
            for (int i = 0; i < dataPairs.size() - 1; i++)
            {
                String uuidKey = keys.getAllValues().get(i).split("\\.")[0];
                String givenKey = keys.getAllValues().get(i);
                String givenData = data.getAllValues().get(i);
                Storage.DataPair dataPair = dataPairs.get(i);

                if (!givenKey.equals(uuidKey + "." + dataPair.getKey()) || !givenData.equals(dataPair.getData()))
                {
                    fail("The datapair (" + dataPair + ") did not match with the received values:" +
                            "\nKey: " + givenKey +
                            "\nData: " + givenData);
                    return;
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // TODO: Test other methods from file storage
}
